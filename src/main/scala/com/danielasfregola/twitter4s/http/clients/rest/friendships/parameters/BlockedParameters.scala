package com.danielasfregola.twitter4s.http.clients.rest.friendships.parameters

import com.danielasfregola.twitter4s.http.marshalling.Parameters

private[twitter4s] final case class BlockedParameters(stringify_ids: Boolean) extends Parameters
