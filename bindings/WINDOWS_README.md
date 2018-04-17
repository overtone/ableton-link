To build for windows, make sure you have VisualStudio kit installed.

* Open DeveloperCommandPrompt
* If compiling for 64 bit platform run vcvars64.bat (in my case `"C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\VC\Auxiliary\Build\vcvars64.bat"`)
* Cd into this directory (bindings) and run the following command (given that `link` git-submodule has been fetched)
```
 cl link_bindings.cc /EHsc /DLINK_PLATFORM_WINDOWS=1 /MD /I"../link/include" /I"../link/modules/asio-standalone/asio/include" /LD
```

If you run into errors, try adding /W4 or /Wall to debug the errors.
