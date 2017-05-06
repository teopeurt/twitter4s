package com.danielasfregola.twitter4s.entities


case class VideoInfo (aspect_ratio: Seq[Int],
                      duration_millis: Long,
                      variants: Seq[VideoInfoVariant])