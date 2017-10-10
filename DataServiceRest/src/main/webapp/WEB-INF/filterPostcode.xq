declare variable $content as xs:string external;
declare variable $name as xs:string external;
let $xml := fn:parse-xml($content)
return element quarters {
  for $q in $xml/quarters/quarter
  return element quarter {
    attribute {"value"} {$q/@value},
    for $p in $q/postcode
    order by (
      if ($name = "C") then
        $p/class/C
      else if ($name = "LR") then
        $p/class/LR
      else if ($name = "MR") then
        $p/class/MR
      else if ($name = "HR") then
        $p/class/HR
      else if ($name = "HC") then
        $p/class/HC
      else if ($name = "MC") then
        $p/class/MC
      else if ($name = "R") then
        $p/class/R
      else if ($name = "learner") then
        $p/primary/learner
      else if ($name = "P1") then
        $p/primary/P1
      else if ($name = "P2") then
        $p/primary/P2
      else if ($name = "unrestricted") then
        $p/primary/unrestricted
      else ()
    )
    return $p
  }
}
