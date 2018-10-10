/**XXX: As the modules of Reha are started as system-processes we need to configure a log for each process.
 * Each self declared main method gets its own config and output file.
 *
 * log/conf/<Classname>.xml
 * log/<classname>.log
 *
 * This is a mess, but better messy logging than none at all.
 */

package logging;