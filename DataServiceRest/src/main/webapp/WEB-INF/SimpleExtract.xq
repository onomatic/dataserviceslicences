declare variable $content as xs:string external;
declare variable $quarter as xs:string external;
declare variable $postcode as xs:string external;
let $xml := fn:parse-xml($content)
return element quarters {
  for $q in $xml/quarters/quarter
  where (if ($quarter) then $q/@value=$quarter else boolean(1))
  return element quarter {
    attribute {"value"} {$q/@value},
    for $element in $q/postcode
    where (if ($postcode) then $element/@value=$postcode else boolean(1))
    return $element
  }  
}