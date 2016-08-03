# ChristmasEngine
A 2d game engine with an example game (`christmas.ChristmasGame`). The game was created last christmas as a joke. It uses the [Processing](processing.org) core libraries, as well as the `sound` and `Box2d` libraries for Processing.

## Command line arguments:
The first argument must be a path to the `resource` directory. After that, the following arguments are possible:
- `-xres VALUE` and `-yres VALUE`: Set the game resolution or window size. Default is the size of the screen.
- `-window` or `-fullscreen`: Run in a window or fullscreen (default).
- `-software` or `-gl`: Use software renderer (usually very slow) or attempt to use OpenGL (default).
- `-smooth VALUE`: Set the smoothing level. For the software renderer, this can be 0 for no smoothing (default), 2 for bilinear, and 3 for bicubic. For OpenGL, this can be 0 for no smoothing (default), 2 for 2x anti-aliasing, 4 for 4x, or 8 for 8x (4x and 8x aren't available on all hardware).
- `-dev`: Turn on developer mode. This shows framerate and other information in the top-left corner.