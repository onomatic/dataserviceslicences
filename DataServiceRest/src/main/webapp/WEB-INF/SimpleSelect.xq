declare variable $names as xs:string+ external;
declare variable $content external;
let $xml := fn:parse-xml($content)
return element quarters {
  for $q in $xml/quarters/quarter
  return element quarter {
    attribute {"value"} {$q/@value},
    for $p in $q/postcode
    return element postcode{
      attribute {"value"} {$p/@value},
      element class{
        for $element in $p/class/*
        return $element[local-name($element) = $names]
      },
      element primary{
        for $element in $p/primary/*
        return $element[local-name($element) = $names]
      }
    }
  }  
}
