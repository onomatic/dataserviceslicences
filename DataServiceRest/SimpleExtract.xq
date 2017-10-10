declare variable $quarter as xs:string external;
declare variable $postcode as xs:string external;
<quarters>
{
for $y in doc("/Users/callumbannister/uni/9322_PRAC/workspace/DataServiceRest/src/main/webapp/license.xml")/quarters/quarter
return <quarter value = "{$y/@value}">{
for $x in $y/postcode
order by $x/@value
where $y/@value=$quarter and (if ($postcode) then $x/@value=$postcode else boolean(1))
return $x
}
</quarter>
}
</quarters>
