# ChristmasEngine
A game engine with 2d graphics, audio, support for changing game speed, and other useful utilities. The engine comes with with an example game (`christmas.ChristmasGame`), which was created last christmas as a joke. The game uses Box2d for physics, which is currently not integrated into the engine.

ChristmasEngine should be compiled with Eclipse. It comes with the [Processing](processing.org) core libraries, as well as the `sound` and `Box2d` libraries for Processing.

## Command line arguments:
The first argument must be a path to the `resource` directory. After that, the following arguments are possible:

- `-xres VALUE` and `-yres VALUE`: Set the game resolution or window size. Default is the size of the screen.
- `-window` or `-fullscreen`: Run in a window or fullscreen (default).
- `-software` or `-gl`: Use software renderer (usually very slow) or attempt to use OpenGL (default).
- `-smooth VALUE`: Set the smoothing level.
	- For the software renderer, this can be:
		- 0 for no smoothing (default)
		- 2 for bilinear smoothing
		- 3 for bicubic
	- For OpenGL, this can be:
		- 0 for no smoothing (default)
		- 2 for 2x anti-aliasing
		- 4 for 4x (not available on all hardware)
		- 8 for 8x (not available on all hardware)
- `-dev`: Turn on developer mode. This shows framerate and other information in the top-left corner.
