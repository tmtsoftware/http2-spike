package com.example

import caseapp.{ ExtraName, HelpMessage }

case class Options(
  @HelpMessage("number of seconds a stream should run for")@ExtraName("d") durationInSeconds: Int,
  @HelpMessage("number of parallel sse requests to spawn")@ExtraName("p") parallelRequests: Int)
