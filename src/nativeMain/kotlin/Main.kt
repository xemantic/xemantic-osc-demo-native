/*
 * xemantic-osc-demo-native - A demonstration of using xemantic-osc library in the Kotlin multiplatform native app
 * Copyright (C) 2023 Kazimierz Pogoda
 *
 * This file is part of xemantic-osc-demo-native.
 *
 * xemantic-osc-demo-native is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * xemantic-osc-demo-native is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with xemantic-osc-demo-native.
 * If not, see <https://www.gnu.org/licenses/>.
 */

import com.xemantic.osc.OscInput
import com.xemantic.osc.route
import com.xemantic.osc.udp.UdpOscTransport
import com.xemantic.osc.udp.discoverLocalIp
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import kotlin.system.exitProcess

data class Vector2(
  val x: Double,
  val y: Double
)

private fun oscInput() = OscInput {
  route<List<Float>>("/accelerometer")
  route<List<Float>>("/magneticfield")
  route<List<Float>>("/orientation")
  route<List<Float>>("/gyroscope")
  route<Float>("/light")
  route<Float>("/pressure")
  route<Float>("/proximity")
  route<List<Float>>("/gravity")
  route<Vector2>(
    address = "/touch*",
    addressMatcher = { address -> address.startsWith("/touch") },
    decoder = {
      Vector2(
        float().toDouble(),
        float().toDouble()
      )
    }
  )
}

@OptIn(ExperimentalStdlibApi::class)
fun main(args: Array<String>) {

  val logger = KotlinLogging.logger {}

  // change for more detailed logging
  KotlinLoggingConfiguration.logLevel = KotlinLoggingLevel.INFO

  logger.info { "Usage: xemantic-osc-demo-native.kexe [port]" }
  logger.info {
    "  where the port is the local UDP port to listen to OSC Messages"
  }

  val port = args.getPort()

  UdpOscTransport(
    port = port,
    input = oscInput()
  ).use { transport ->
    logger.info {
      "Sensors2OSC config - host: ${discoverLocalIp()}, port: ${transport.peer.port}"
    }
    runBlocking {
      transport.input.messages.collect {
        logger.info { "OscMessage: $it"}
      }
    }
  }
}

private fun Array<String>.getPort() = if (isNotEmpty()) {
  try {
    this[0].toInt()
  } catch (e : NumberFormatException) {
    exitProcess(1)
  }
} else {
  0
}
