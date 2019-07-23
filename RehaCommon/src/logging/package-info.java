/**
 * XXX: As the modules of Reha are started as system-processes we need to
 * configure a log for each process. Each self declared main method gets its own
 * config and output file.
 *
 * log/conf/<Classname>.xml log/<classname>.log
 *
 * This is a mess, but better messy logging than none at all.
 * 
 * 
 * to make a a Process start logging call new Logging("<logfilename>") this will
 * call <logfilename>.xml for configuration and write to log/<logfilename>
 * defined in the xml
 */

package logging;