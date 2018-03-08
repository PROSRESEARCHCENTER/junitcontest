package hep.io.root.daemon.xrootd;

class XrootdProtocol
{
   final static int defaultPort = 1094;
   
   final static int kXR_DataServer = 1;
   final static int kXR_LBalServer = 0;
   final static int kXR_maxReqRetry = 10;
   
   final static int  kXR_auth    =  3000;
   final static int  kXR_query   =  3001;
   final static int  kXR_chmod   =  3002;
   final static int  kXR_close   =  3003;
   final static int  kXR_dirlist =  3004;
   final static int  kXR_getfile =  3005;
   final static int  kXR_protocol=  3006;
   final static int  kXR_login   =  3007;
   final static int  kXR_mkdir   =  3008;
   final static int  kXR_mv      =  3009;
   final static int  kXR_open    =  3010;
   final static int  kXR_ping    =  3011;
   final static int  kXR_putfile =  3012;
   final static int  kXR_read    =  3013;
   final static int  kXR_rm      =  3014;
   final static int  kXR_rmdir   =  3015;
   final static int  kXR_sync    =  3016;
   final static int  kXR_stat    =  3017;
   final static int  kXR_set     =  3018;
   final static int  kXR_write   =  3019;
   final static int  kXR_admin   =  3020;
   final static int  kXR_prepare =  3021;
   final static int  kXR_statx   =  3022;
   final static int  kXR_endsess =  3023;
   final static int  kXR_bind    =  3024;
   final static int  kXR_readv   =  3025;
   final static int  kXR_verifyw =  3026;
   final static int  kXR_locate  =  3027;
   final static int  kXR_truncate=  3028;

   
   final static int  kXR_ok       = 0;
   final static int  kXR_oksofar  = 4000;
   final static int  kXR_attn     = 4001;
   final static int  kXR_authmore = 4002;
   final static int  kXR_error    = 4003;
   final static int  kXR_redirect = 4004;
   final static int  kXR_wait     = 4005;
   final static int  kXR_waitresp = 4006;
   

   final static int kXR_asyncab   = 5000;
   final static int kXR_asyndi    = 5001;
   final static int kXR_asyncms   = 5002;
   final static int kXR_asyncrd   = 5003;
   final static int kXR_asyncwt   = 5004;
   final static int kXR_asyncav   = 5005;
   final static int kXR_asynunav  = 5006;
   final static int kXR_asynresp  = 5008;
   
   final static int kXR_ur = 0x100;
   final static int kXR_uw = 0x080;
   final static int kXR_ux = 0x040;
   final static int kXR_gr = 0x020;
   final static int kXR_gw = 0x010;
   final static int kXR_gx = 0x008;
   final static int kXR_or = 0x004;
   final static int kXR_ow = 0x002;
   final static int kXR_ox = 0x001;
   
   final static int kXR_file = 0;
   final static int kXR_xset = 1;
   final static int kXR_isDir = 2;
   final static int kXR_other = 4;
   final static int kXR_offline = 8;
   final static int kXR_readable = 16;
   final static int kXR_writable = 32;
   
   final static int kXR_compress = 1;
   final static int kXR_delete   = 2;
   final static int kXR_force    = 4;
   final static int kXR_new      = 8;
   final static int kXR_open_read= 16;
   final static int kXR_open_updt= 32;
   final static int kXR_async    = 64;
   final static int kXR_refresh  = 128;
   final static int kXR_mkpath   = 256;
   final static int kXR_open_apnd= 512;
   final static int kXR_retstat  = 1024;
   final static int kXR_replica  = 2048;
   final static int kXR_ulterior = 4096;
   final static int kXR_nowait   = 8192;
   
   final static int kXR_cancel = 1;
   final static int kXR_notify = 2;
   final static int kXR_noerrs = 4;
   final static int kXR_stage  = 8;
   final static int kXR_wmode  = 16;
   
   final static int kXR_useruser = 0;
   final static int kXR_useradmin = 1;
   
   final static int kXR_QStats = 1;
   final static int kXR_QPrep = 2; 
   final static int kXR_Qcksum = 3; 
   final static int kXR_Qxattr = 4;
   final static int kXR_Qspace = 5;
   final static int kXR_Qckscan= 6;
   final static int kXR_Qconfig= 7;
   final static int kXR_Qvisa  = 8;
   
   final static int kXR_asyncap = 128;
   
   final static int XRD_CLIENT_CURRENTVER = 2; //???
}
