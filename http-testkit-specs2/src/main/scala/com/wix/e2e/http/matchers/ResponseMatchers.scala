package com.wix.e2e.http.matchers

import com.wix.e2e.http.matchers.internal._

trait ResponseMatchers extends ResponseStatusMatchers
                          with ResponseCookiesMatchers
                          with ResponseHeadersMatchers
                          with ResponseSpecialHeadersMatchers
                          with ResponseBodyMatchers
                          with ResponseBodyAndStatusMatchers
                          with ResponseStatusAndHeaderMatchers
                          with ResponseTransferEncodingMatchers

object ResponseMatchers extends ResponseMatchers
