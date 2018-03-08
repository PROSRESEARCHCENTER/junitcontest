#
# Makefile for the Aida library
#
# Author: Mark Donszelmann
# Version: $Id: GNUmakefile 8611 2006-08-16 06:51:18Z duns $
#

ifneq ($(CERN_ROOT),)

#
# User Configuration: Define targets
#
OBJS = minuitAdapter
DLLS = minuitAdapter
LINKNOCXX = true
LINKNOJAVA = true
LINKFORTRAN = true

DNAME = $(DLLS)-3.0.6-$(DST)$(TAREXT)
DDIR  = ../../../../../../LICENSE.txt
DDIR += ../../../../../../LGPL.txt
DINC =
DSLIB  = $(DST)/*$(DLLEXT) $(DST)/*$(LIBEXT)

endif   # CERN_ROOT

#
# No User Configuration below
#

include $(FREEHEP)/config/common.gmk
