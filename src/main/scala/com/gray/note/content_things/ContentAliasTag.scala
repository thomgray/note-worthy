package com.gray.note.content_things

class ContentAliasTag(bodyString: String, headString: String) extends ContentTagLikeThing(bodyString, headString){
   private var _isParentVisible: Boolean = _
   private var _isUniversallyReferenced: Boolean = _
   private var _isParaphrase = true
}
