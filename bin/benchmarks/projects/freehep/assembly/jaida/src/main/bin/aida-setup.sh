#
# Setup for Unix (Linux, Solaris, MacOS X, Windows-Cygwin) using sh
#

# check for JAIDA_HOME
if test ! "${JAIDA_HOME:+x}"
then
    echo "JAIDA_HOME environment variable not set!"
    return
fi

# Unix or Cygwin

case `uname` in
CYGWIN*)
        CLASSPATHSEP=';'
        UJAIDA_HOME=`cygpath -u ${JAIDA_HOME}`
        MJAIDA_HOME=`cygpath -m ${JAIDA_HOME}`
        ;;
*)
        CLASSPATHSEP=':'
        UJAIDA_HOME="${JAIDA_HOME}"
        MJAIDA_HOME="${JAIDA_HOME}"
esac


# Check JAIDA_HOME is set sensibly

if ! test -e "${UJAIDA_HOME}/lib"
then
   echo "The lib directory seem to be missing, make sure JAIDA_HOME has been set properly"
   return
fi

JAIDAJARS=`ls -w1 ${UJAIDA_HOME}/lib | grep .jar`

for jar in ${JAIDAJARS}; do
  CLASSPATH="${MJAIDA_HOME}/lib/${jar}${CLASSPATHSEP}${CLASSPATH}"
done
export CLASSPATH

# AOL

case `uname` in
Darwin)
case `uname -p` in
i386)		
        AOL="i386-MacOSX-g++"
        ;;
*)
		AOL="ppc-MacOSX-g++"
esac
;;
Linux)
        AOL="i386-Linux-g++"
        ;;
SunOS)
        AOL="sparc-SunOS-CC"
        ;;
CYGWIN*)
        AOL="i386-Windows-g++"
        ;;
MINGW32*)
        AOL="i386-Windows-g++"
        ;;
*)
        echo "AOL (Architecture-OS-Linker) cannot be derived, please edit setup script"
        return
esac

case `uname` in
Darwin)
		DYLD_LIBRARY_PATH="${DYLD_LIBRARY_PATH}:${UJAIDA_HOME}/lib/${AOL}"
		export DYLD_LIBRARY_PATH
		;;
*)
		LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${UJAIDA_HOME}/lib/${AOL}"
		export LD_LIBRARY_PATH
esac
