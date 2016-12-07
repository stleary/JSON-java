#JSON in Java [package org.json]

###Douglas Crockford

douglas@crockford.com

2011-02-02

JSON은 경량화된,언어의 독립적인,데이터교환 포맷입니다.

http://www.JSON.org/ 를 참고하세요.

이 패키지안에 있는 파일은 Java에서 JSON encoders/decoders를 구현합니다. 

또한 JSON과 XMLXML, HTTP,headers, Cookies, and CDL 사이를 변환할 수 있는 능력도 있습니다. 

이 것은 레퍼런스 구현입니다. 자바안에 많은 JSON 패키지들이 있습니다. 
아마 언젠가 자바 커뮤니티는 그것을 하나로 표준화할 것입니다. 
그때까지, 신중하게 선택하세요.


이 라이센스는 아래와 같은 제한이 있습니다.
>"이 소프트웨어는 악의적인 용도로 사용할 수 없습니다."

만약 악의적인 용도로 사용하실거면 다른 패키지를 선택하세요.

이 패키지는 Java 1.8에서 컴파일합니다.

####JSONObject.java :
JSONObject는 String이나, Map가 같은 객체를 생성하는 JSONTokener
에서 텍스트를 파싱할 수 있습니다. 이 오브젝트는 이것들의 콘텐츠를 다루고,JSON을 호환하는 객체 직렬화를 만들기위한 메소드를 제공합니다.

####JSONArray.java  : 
JSONArray는 String이나, Vector 같은 객체를 생성하느 JSONTokener에서 텍스트를 파싱할 수 있습니다. 이 오브젝트는 이것들의 콘텐츠를 다루고,JSON을 호환하는 객체 직렬화를 만들기위한 메소드를 제공합니다.


####JSONTokener.java  : 
JSONTokener는 텍스트를 각각의 토큰순으로 쪼갭니다.이것은 String,Reader,InputStream으로 구성할 수가 있습니다.

####JSONException.java : 
JSONException는 이 패키지에서 발생한 표준 예외타입입니다.

####JSONString.java : 
JSONString 인터페이스는 객체가 직력화를 제공하도록 하용하는 toJSONString 메소드를 제공합니다.

####JSONStringer.java : 
JSONStringer은 JSON 문자열들을 빌드하는데 편리한 기능을 제공합니다.

####JSONWriter.java : 
JSONWriter는 writer을 통해서 JSON 텍스트를 빌드하는데 편리한 기능을 제공합니다.

####CDL.java: 
CDL은 JSON과 콤마로 구분된 리스트들 사이를 변환하는 기능을 제공해줍니다.

####Cookie.java: 
Cookie는 JSON과 Cookie들 사이를 변환하는 기능을 제공합니다.

####CookieList.java:
CookieList는 JSON과 Cookie는 리스트들 사이를 변환하는 기능을 제공합니다.

####HTTP.java: 
HTTP 는 JSON 과  HTTP 헤더들 사이를 변환하는 기능을 제공합니다.

####HTTPTokener.java : 
HTTPTokener는 HTTP 헤더를 파싱하기 위해 JSONTokener를 확장한 것입니다.

####XML.java: 
XML.java는 JSON 과 XML사이를 변환하는 기능을 제공합니다.

####JSONML.java: 
JSONML.java는 JSONML 과 XML사이를 변환하는 기능을 제공합니다.

####XMLTokener.java: 
XMLTokener.java는 XML 텍스트를 파싱하기위해 JSONTokener를 확장한 것입니다. 




[http://www.JSON.org/]:http://babdev.blogspot.co.at
