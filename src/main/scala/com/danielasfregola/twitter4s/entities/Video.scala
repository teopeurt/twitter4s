package com.danielasfregola.twitter4s.entities

final case class Video(aspect_ratio: Seq[Int],
                 duration_millis: Long,
                 variants: Seq[VideoVariant] )
