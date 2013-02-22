package com.acme.platform.model

import org.specs2.mutable.Specification

class DummyCaseClassSpec extends Specification {

  "DummyCaseClass" should {
    "wrap an underlying string" in {
      val dcc = DummyCaseClass("hello")
      dcc must not beNull
      val underlying = dcc.underlying
      underlying must have size(5)
      underlying must equalTo("hello")
    }
  }

}
