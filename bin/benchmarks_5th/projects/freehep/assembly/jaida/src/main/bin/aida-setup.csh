#
# Setup for Unix (Linux, Solaris, MacOS X, Windows-Cygwin) using csh
#

# Check JAIDA_HOME is set sensibly

if (! $?JAIDA_HOME) then
   echo "JAIDA_HOME has not been set"
   exit
endif


# Unix or Cygwin

switch(`uname`)
case CYGWIN*:
    set CLASSPATHSEP=';'
    set UJAIDA_HOME=`cygpath -u ${JAIDA_HOME}`
    set MJAIDA_HOME=`cygpath -m ${JAIDA_HOME}`
    breaksw
default:
    set CLASSPATHSEP=':'
    set UJAIDA_HOME="${JAIDA_HOME}"
    set MJAIDA_HOME="${JAIDA_HOME}"
    breaksw
endsw

if (! -e "${UJAIDA_HOME}/lib") then
   echo "The lib directory seem to be missing, make sure JAIDA_HOME has been set properly"
   exit
endif

set JAIDAJARS=`ls -w1 ${UJAIDA_HOME}/lib | grep .jar`

if (! $?CLASSPATH) then
   setenv CLASSPATH ""
endif

foreach jar (${JAIDAJARS})
   setenv CLASSPATH "${MJAIDA_HOME}/lib/${jar}${CLASSPATHSEP}${CLASSPATH}"
end

# AOL

switch(`uname`)
case Darwin:
switch(`uname -p`)
case i386:		
        set AOL="i386-MacOSX-g++"
        breaksw
default:
		set AOL="ppc-MacOSX-g++"
		breaksw
endsw
breaksw
case Linux:
        set AOL="i386-Linux-g++"
        breaksw
case SunOS:
        set AOL="sparc-SunOS-CC"
        breaksw
case CYGWIN*:
        set AOL="i386-Windows-g++"
        breaksw
case MINGW32*:
        set AOL="i386-Windows-g++"
        breaksw
default:
        echo "AOL (Architecture-OS-Linker) cannot be derived, please edit setup script"
        return
        breaksw
endsw

switch(`uname`)
case Darwin:
		if (! $?DYLD_LIBRARY_PATH) then
   			setenv DYLD_LIBRARY_PATH ""
		endif
		setenv DYLD_LIBRARY_PATH "${DYLD_LIBRARY_PATH}:${UJAIDA_HOME}/lib/${AOL}"
		breaksw
default:
		if (! $?LD_LIBRARY_PATH) then
   			setenv LD_LIBRARY_PATH ""
		endif
		setenv LD_LIBRARY_PATH "${LD_LIBRARY_PATH}:${UJAIDA_HOME}/lib/${AOL}"
		breaksw
endsw

end:

