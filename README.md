# wurzelimperium-tools
Lots of macros and macro generating tools, perfect for ReMouse and Fiddler to automate this boring game.
The project contains Java projects (IntelliJ) and macros for automation purposes:

<b>IntelliJ:</b>
* RandfelderSpalte17OhneMaulwurf - generate macro for column 17 under the assumption that there are no moles in any of the gardens
* ReMouse Makro-Generator - generates the desired ReMouse macros, allowing for variation of plant width / height and garden type
* WimpRechner - supposed to be a calculator for products that Wimps (people coming to your garden) want to buy to find out if it's a good deal, unfinished
* WurzelimperiumCMD - on a command-line basis, allows for either of: generate coins, redeem coins, fulfill infinitequests, view autokino ad (that's a different kind of ad compared to the ads needed for coins). Programmed with Java Stream API and HTTP request API, including authentication-related stuff if necessary
* WurzelimperiumWeb - same as WurzelimperiumCMD but with a webserver (Tomcat with jsp files, as far as I recall)

<b>Macros:</b>
* Kratonic Mouse and Key Recorder - Kratonic macros, for different devices, browsers, resolutions, normal/premium (premium doesn't have the bar at the top, so lower resolution overall) for numerous purposes (grow plant, watch video, water plant, up to fully automated bots that play the game by itself on a computer if you let them run indefinitely (i.e. grow carrots, which only take 6 mins until they can get harvested))
* ReMouse Standard - same, but for ReMouse - it's faster than Kratonic, but not as wysiwyg as it (harder to see the commands needed to write a proper macro, more like "record it and it'll be saved in a file that you won't need to touch ever again anymore")
* ScheduledTasksOutput - just the results of deploying tasks on my NAS, such as watching 500 videos once every 24h or planting herbs every 7hrs or whatever
