The files contained in this folder contain the full source of the firmware running on Brainlink's ATXmega16a4 controller. You are free to modify the Brainlink's firmware as desired.

Each source file is commented, but you may wish to begin exploring the code with mainFirmware.c, which is the location of the main function and the implementation of the communications protocol. All other files contain helper functions addressing specific modules of the Brainlink.

In order to customize the firmware, you will need to download a development environment for Atmel's line of Xmega controllers. Instructions for doing so are available in the customizing firmware tutorial at:
http://www.brainlinksystem.com/tutorials