package blueeyes.core.service.test

import org.specs.Specification
import blueeyes.core.service.RestPathPatternImplicits._
import blueeyes.core.service._
import blueeyes.util.Future
import blueeyes.core.data.TextToTextBijection
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.HttpStatusCodes._
import blueeyes.core.http.{HttpMethod, HttpVersion, HttpMethods, HttpVersions, HttpRequest, HttpResponse, HttpStatusCode, HttpStatus, HttpStatusCodes, MimeType}

class BlueEyesServiceSpecificationSampleSpec extends Specification with BlueEyesServiceSpecification[String]{
  val service = new SampleService()

  "SampleService when using GET" should {
    "/get/'foo should return foo value as response content" in {
      path("/get/foo-value"){
        get{
          status  mustEqual(HttpStatus(OK))
          content mustEqual(Some("foo-value"))
        }
      }
    }
  }
  "SampleService when using POST" should {
    "/post/foo should return request content as response content" in {
      path("/post/foo"){
        post({
          status  mustEqual(HttpStatus(OK))
          content mustEqual(Some("post-content"))
        }, Map(), Map(), Some("post-content"))
      }
    }
  }
}

class SampleService extends RestHierarchyBuilder[String] {
  private implicit val transcoder = new HttpStringDataTranscoder(TextToTextBijection, text / html)

  path("/get/'foo") {get(new GetHandler())}
  path("/post/foo") {post(new PutHandler())}

  class GetHandler extends Function1[HttpRequest[String], Future[HttpResponse[String]]]{
    def apply(request: HttpRequest[String]) = {
      val fooValue = request.parameters.get('foo).getOrElse("")
      val response = HttpResponse[String](HttpStatus(HttpStatusCodes.OK), Map("Content-Type" -> "text/plain"), Some(fooValue))
      new Future[HttpResponse[String]]().deliver(response)
    }
  }
  class PutHandler extends Function1[HttpRequest[String], Future[HttpResponse[String]]]{
    def apply(request: HttpRequest[String]) = {
      val response = HttpResponse[String](HttpStatus(HttpStatusCodes.OK), Map("Content-Type" -> "text/plain"), request.content)
      new Future[HttpResponse[String]]().deliver(response)
    }
  }
}
