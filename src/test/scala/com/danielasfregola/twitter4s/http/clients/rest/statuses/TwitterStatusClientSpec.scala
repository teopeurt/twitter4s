package com.danielasfregola.twitter4s.http.clients.rest.statuses

import akka.http.scaladsl.model.{HttpEntity, HttpMethods}
import com.danielasfregola.twitter4s.entities._
import com.danielasfregola.twitter4s.helpers.ClientSpec

class TwitterStatusClientSpec extends ClientSpec {

  class TwitterStatusClientSpecContext extends RestClientSpecContext with TwitterStatusClient

  "Twitter Status Client" should {

    "perform a mentions timeline request" in new TwitterStatusClientSpecContext {
      val result: RatedData[Seq[Tweet]] = when(mentionsTimeline(count = 10)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/mentions_timeline.json"
        request.uri.queryString() === Some("contributor_details=false&count=10&include_entities=true&trim_user=false")
      }.respondWithRated("/twitter/rest/statuses/mentions_timeline.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[Seq[Tweet]]("/fixtures/rest/statuses/mentions_timeline.json")
    }

    "perform a user timeline request by screen name" in new TwitterStatusClientSpecContext {
      val result: RatedData[Seq[Tweet]] = when(userTimelineForUser(screen_name = "DanielaSfregola", count = 10)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/user_timeline.json"
        request.uri.queryString() === Some("contributor_details=false&count=10&exclude_replies=false&include_rts=true&screen_name=DanielaSfregola&trim_user=false")
      }.respondWithRated("/twitter/rest/statuses/user_timeline.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[Seq[Tweet]]("/fixtures/rest/statuses/user_timeline.json")
    }

    "perform a user timeline request by user id" in new TwitterStatusClientSpecContext {
      val result: RatedData[Seq[Tweet]] = when(userTimelineForUserId(user_id = 123456L , count = 10)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/user_timeline.json"
        request.uri.queryString() === Some("contributor_details=false&count=10&exclude_replies=false&include_rts=true&trim_user=false&user_id=123456")
      }.respondWithRated("/twitter/rest/statuses/user_timeline.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[Seq[Tweet]]("/fixtures/rest/statuses/user_timeline.json")
    }

    "perform a home timeline request" in new TwitterStatusClientSpecContext {
      val result: RatedData[Seq[Tweet]] = when(homeTimeline(count = 10)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/home_timeline.json"
        request.uri.queryString() === Some("contributor_details=false&count=10&exclude_replies=false&include_entities=true&trim_user=false")
      }.respondWithRated("/twitter/rest/statuses/home_timeline.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[Seq[Tweet]]("/fixtures/rest/statuses/home_timeline.json")
    }

    "perform a retweets of me request" in new TwitterStatusClientSpecContext {
      val result: RatedData[Seq[Tweet]] = when(retweetsOfMe(count = 10)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/retweets_of_me.json"
        request.uri.queryString() === Some("contributor_details=false&count=10&exclude_replies=false&include_entities=true&trim_user=false")
      }.respondWithRated("/twitter/rest/statuses/retweets_of_me.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[Seq[Tweet]]("/fixtures/rest/statuses/retweets_of_me.json")
    }

    "perform a retweets request" in new TwitterStatusClientSpecContext {
      val id = 648866645855879168L
      val result: RatedData[Seq[Tweet]] = when(retweets(id, count = 10)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === s"https://api.twitter.com/1.1/statuses/retweets/$id.json"
        request.uri.queryString() === Some("count=10&trim_user=false")
      }.respondWithRated("/twitter/rest/statuses/retweets.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[Seq[Tweet]]("/fixtures/rest/statuses/retweets.json")
    }

    "perform a show tweet request" in new TwitterStatusClientSpecContext {
      val result: RatedData[Tweet] = when(getTweet(648866645855879168L)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/show.json"
        request.uri.queryString() === Some("id=648866645855879168&include_entities=true&include_my_retweet=false&trim_user=false")
      }.respondWithRated("/twitter/rest/statuses/show.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[Tweet]("/fixtures/rest/statuses/show.json")
    }

    "send a status update" in new TwitterStatusClientSpecContext {
      val result: Tweet = when(createTweet("This is a test")).expectRequest { request =>
        request.method === HttpMethods.POST
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/update.json"
        request.entity === HttpEntity(
          `application/x-www-form-urlencoded`,
          "display_coordinates=false&possibly_sensitive=false&status=This+is+a+test&trim_user=false")
      }.respondWith("/twitter/rest/statuses/update.json").await
      result === loadJsonAs[Tweet]("/fixtures/rest/statuses/update.json")
    }

    "send a status update with some media" in new TwitterStatusClientSpecContext {
      val result: Tweet = when(createTweet("This is a test", media_ids = Seq(1L, 2L))).expectRequest { request =>
        request.method === HttpMethods.POST
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/update.json"
        request.entity === HttpEntity(
          `application/x-www-form-urlencoded`,
          "display_coordinates=false&media_ids=1%2C2&possibly_sensitive=false&status=This+is+a+test&trim_user=false")
      }.respondWith("/twitter/rest/statuses/update.json").await
      result === loadJsonAs[Tweet]("/fixtures/rest/statuses/update.json")
    }

    "send direct message as tweet" in new TwitterStatusClientSpecContext {
      val result: Tweet = when(createDirectMessageAsTweet("This is a test for a direct message", "DanielaSfregola")).expectRequest { request =>
        request.method === HttpMethods.POST
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/update.json"
        request.entity === HttpEntity(
          `application/x-www-form-urlencoded`,
          "display_coordinates=false&possibly_sensitive=false&status=D+DanielaSfregola+This+is+a+test+for+a+direct+message&trim_user=false")
      }.respondWith("/twitter/rest/statuses/direct_message.json").await
      result === loadJsonAs[Tweet]("/fixtures/rest/statuses/direct_message.json")
    }

    "delete an existing tweet" in new TwitterStatusClientSpecContext {
      val id = 648866645855879168L
      val result: Tweet = when(deleteTweet(id)).expectRequest { request =>
        request.method === HttpMethods.POST
        request.uri.endpoint === s"https://api.twitter.com/1.1/statuses/destroy/$id.json"
        request.uri.queryString() === Some("trim_user=false")
      }.respondWith("/twitter/rest/statuses/destroy.json").await
      result === loadJsonAs[Tweet]("/fixtures/rest/statuses/destroy.json")
    }

    "retweet a tweet" in new TwitterStatusClientSpecContext {
      val id = 648866645855879168L
      val result: Tweet = when(retweet(id)).expectRequest { request =>
        request.method === HttpMethods.POST
        request.uri.endpoint === s"https://api.twitter.com/1.1/statuses/retweet/$id.json"
        request.uri.queryString() === Some("trim_user=false")
      }.respondWith("/twitter/rest/statuses/retweet.json").await
      result === loadJsonAs[Tweet]("/fixtures/rest/statuses/retweet.json")
    }

    "get a tweet by id in oembed format " in new TwitterStatusClientSpecContext {
      val result: RatedData[OEmbedTweet] = when(oembedTweetById(648866645855879168L)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/oembed.json"
        request.uri.queryString() === Some("align=none&hide_media=false&hide_thread=false&hide_tweet=false&id=648866645855879168&lang=en&omit_script=false")
      }.respondWithRated("/twitter/rest/statuses/oembed.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[OEmbedTweet]("/fixtures/rest/statuses/oembed.json")
    }

    "get a tweet by url in oembed format " in new TwitterStatusClientSpecContext {
      val url = s"https://twitter.com/Interior/status/648866645855879168"
      val result: RatedData[OEmbedTweet] = when(oembedTweetByUrl(url)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/oembed.json"
        request.uri.queryString() === Some("align=none&hide_media=false&hide_thread=false&hide_tweet=false&lang=en&omit_script=false&url=https://twitter.com/Interior/status/648866645855879168")
      }.respondWithRated("/twitter/rest/statuses/oembed.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[OEmbedTweet]("/fixtures/rest/statuses/oembed.json")
    }

    "get retweeters ids" in new TwitterStatusClientSpecContext {
      val result: RatedData[UserIds] = when(retweeterIds(327473909412814850L)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/retweeters/ids.json"
        request.uri.queryString() === Some("count=100&cursor=-1&id=327473909412814850&stringify_ids=false")
      }.respondWithRated("/twitter/rest/statuses/retweeters_ids.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[UserIds]("/fixtures/rest/statuses/retweeters_ids.json")
    }

    "get retweeters ids stringified" in new TwitterStatusClientSpecContext {
      val result: RatedData[UserStringifiedIds] = when(retweeterStringifiedIds(327473909412814850L)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/retweeters/ids.json"
        request.uri.queryString() === Some("count=100&cursor=-1&id=327473909412814850&stringify_ids=true")
      }.respondWithRated("/twitter/rest/statuses/retweeters_ids_stringified.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[UserStringifiedIds]("/fixtures/rest/statuses/retweeters_ids_stringified.json")
    }

    "perform a lookup" in new TwitterStatusClientSpecContext {
      val result: RatedData[Seq[LookupTweet]] = when(tweetLookup(327473909412814850L, 327473909412814851L)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/lookup.json"
        request.uri.queryString() === Some("id=327473909412814850,327473909412814851&include_entities=true&map=false&trim_user=false")
      }.respondWithRated("/twitter/rest/statuses/lookup.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[Seq[LookupTweet]]("/fixtures/rest/statuses/lookup.json")
    }

    "reject request if no ids have been provided for the lookup" in new TwitterStatusClientSpecContext {
      tweetLookup() must throwA[IllegalArgumentException]("requirement failed: please, provide at least one status id to lookup")
    }

    "perform a mapped lookup" in new TwitterStatusClientSpecContext {
      val result: RatedData[LookupMapped] = when(tweetLookupMapped(327473909412814850L, 327473909412814851L)).expectRequest { request =>
        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/lookup.json"
        request.uri.queryString() === Some("id=327473909412814850,327473909412814851&include_entities=true&map=true&trim_user=false")
      }.respondWithRated("/twitter/rest/statuses/lookup_mapped.json").await
      result.rate_limit === rateLimit
      result.data === loadJsonAs[LookupMapped]("/fixtures/rest/statuses/lookup_mapped.json")
    }

    "get video url" in new TwitterStatusClientSpecContext {
      val result: RatedData[Tweet] = when(getTweet(560049149836808192L)).expectRequest { request =>

        request.method === HttpMethods.GET
        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/show.json"
        request.uri.queryString() === Some("id=560049149836808192&include_entities=true&include_my_retweet=false&trim_user=false")

      }.respondWithRated("/twitter/rest/statuses/lookup_media_video.json").await
      result.rate_limit === rateLimit

      val entities = result.data.extended_entities.get

      val media = entities.media.seq.head

      val video = media.video_info.variants.head

      video.url === "https:\\/\\/video.twimg.com\\/ext_tw_video\\/560049056895209473\\/pu\\/pl\\/udxtIM8FytsAE4HQ.m3u8"

      result.data === loadJsonAs[Tweet]("/fixtures/rest/statuses/lookup_media_video.json")
    }

//    "perform a show tweet request" in new TwitterStatusClientSpecContext {
//      val result: RatedData[Tweet] = when(getTweet(648866645855879168L)).expectRequest { request =>
//        request.method === HttpMethods.GET
//        request.uri.endpoint === "https://api.twitter.com/1.1/statuses/show.json"
//        request.uri.queryString() === Some("id=648866645855879168&include_entities=true&include_my_retweet=false&trim_user=false")
//      }.respondWithRated("/twitter/rest/statuses/show.json").await
//      result.rate_limit === rateLimit
//      result.data === loadJsonAs[Tweet]("/fixtures/rest/statuses/show.json")
//    }


  }
}

