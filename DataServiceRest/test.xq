declare variable $quarter as xs:string external;
copy $c := doc("/Users/callumbannister/uni/9322_PRAC/workspace/DataServiceRest/src/main/webapp/license.xml")
modify (
  for $x in $c/quarters/quarter
  where $x/@value!=$quarter
  return delete node $x
)
return $c
