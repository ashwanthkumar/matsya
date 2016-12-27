package com.indix.matsya

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

trait Verifier {
  def hasViolated: Boolean
  def isPriceViolation: Boolean
}

class DefaultVerifier(clusterConfig: ClusterConfig, state: State) extends Verifier {
  private val logger = Logger(LoggerFactory.getLogger(getClass))

  lazy val hasViolated = isPriceViolation && hasViolatedMaxTimes

  lazy val hasViolatedMaxTimes = {
    (state.nrOfTimes + 1) > clusterConfig.maxNrOfTimes
  }

  lazy val isPriceViolation = {
    val currentThreshold = state.price / clusterConfig.maxBidPrice
    logger.info(s"CurrentThreshold = ${math.round(currentThreshold * 100)}%, AcceptableThreshold = ${math.round(clusterConfig.maxThreshold * 100)}%, BidPrice = $$${clusterConfig.maxBidPrice} in AZ = ${state.az}")
    currentThreshold > clusterConfig.maxThreshold
  }
}
