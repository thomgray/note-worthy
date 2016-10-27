package cucumber.steps

import com.gray.note.NoteHandler
import com.gray.note.Config._
import com.gray.note.content_things.ContentTag

class RetrievingSteps extends BaseSteps {

  When("""^I search for the aws note$""") { () =>
    resultHolder.contentTags = Some(NoteHandler(liveNotesDirectory).tagsMatchingLabel("aws"))
  }
  Then("""^The result is the aws note$""") { () =>
    val tags: Array[ContentTag] = resultHolder.contentTags.get
    tags.length mustBe 1
    println(tags(0))
  }

  When("""^I type a label followed by -i$""") { () =>

  }

  Then("""^I see the index for the given label$""") { () =>
  }

}
