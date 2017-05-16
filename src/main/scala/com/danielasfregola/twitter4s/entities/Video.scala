package com.danielasfregola.twitter4s.entities

case class Video(aspect_ratio: Seq[Int],
                 duration_millis: Long,
                 variants: Seq[VideoVariant] )
