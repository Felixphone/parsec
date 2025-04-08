This progect is intended to be a game engine (written as mutch from scratch as possible) for a space themed exploration game.

I decided to split the engine up into different threads to help improve overhead.
This is important as in the future I want to upscale it, and create a separate, native graphics engine which will interface with the main engine via the JNI.

There are 3 main threads:
- the ENGINE_CORE_THREAD: handles the engine stuff
- the WATCHDOG_THREAD: looks after the engine core and handles exits and crashes ext.
- the LOGGER_THREAD: to process and record log entries submitted by both the watchdog and engine

The main instantiation structure is as follows:

    Launcher: launches the program and creates an instance of both the EngineWatchdog and EngineCore
        ├ EngineWatchdog: runs on the separate WATCHDOG_THREAD and manages exit handling / crashes
        |    ├ Logger: provides methods to log to a Log object. Runs a separate task on the LOGGER_THREAD, to process log entries stored in a BlockingQueue, and add them to the Log
        |    |    ├    Log: records and outputs log entries
        |
        ├ EngineCore: runs on the CORE_ENGINE_THREAD and handles graphics, and updates
        |    ├ GameCore: holds the main logic for the game

Current issues faced (at least this is what I think they are, I am not too sure):
- Unexpected exiting of threads with no exception thrown
- Unexpected freezing of threads with no exception thrown
- Threads failing to start with no exception thrown
- Frequent NullPointerExceptions thrown whilst trying to share objects between threads (i managed to comment a few)
- ConcurrentModificationExceptions thrown again whilst trying to access objects from different threads
- Threads failing to acnowladge updated variables set from other threads unless breakpoint set at expression check? (namely the EngineWatchdog failing to acknowladge that the exitReport variable is no longer null, and the Logger failing to acknowladge that the BlockingQueue is no longer empty)

I am pretty sure I am structuring my thread interactions completely wrong. If you could look over the code and its structure I would be very gratefull. Thanks!

Some usefull info:
- Press '0' to quit
- Press 'SHIFT' + '0' to throw a SimulatedException and crash
- Launcher arguments can be passed in. Setting the LogLevel to INFO or SUCCESS might make the console less cluttered. (documentation of inputs in Logger.java file)
- Documentation is available here: https://felixphone.github.io/parsec/
