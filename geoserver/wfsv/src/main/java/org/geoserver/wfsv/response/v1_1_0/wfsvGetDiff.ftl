<#-- 
HTML output for feature differences. Warning, this template is complex!
-->
<html>
<body>
<#-- Diff has multiple DifferenceQuery elements, so we'll have multiple list of 
     differences in the  general case -->
<#list queryDiffs as queryDiff>
<h2>Feature type '${queryDiff.typeName}', 
diff from version ${queryDiff.fromVersion}  to version ${queryDiff.toVersion} </h2>
  <#assign differenceFound=false> 
  
  <#-- scan thru each single difference, and report changes -->
  <#list queryDiff.differences as difference>
    <#assign differenceFound=true> 
    <p>Feature ${difference.ID},  
    <#if difference.state = 0>
      inserted, feature content:
      <ul>
      <#list difference.feature.attributes as attribute>
        <li>${attribute.name}: ${attribute.value}
      </#list>
      </ul>
    </#if>
    <#if difference.state = 1>
      updated, modified attributes:<br>
      <table border="1" cellspacing="0">
      <tr><td>Attribute</td><td>Value at ${queryDiff.fromVersion}</td><td>Value at ${queryDiff.toVersion}</td></tr>
      <#list difference.changedAttributes as attName>
        <tr><td>${attName}</td><td>${difference.oldFeature.attributes[attName].value}</td><td>${difference.feature.attributes[attName].value}</td></tr>
      </#list>
      </table>
      </ul>
    </#if>
    <#if difference.state = 2>
      deleted, old feature content:
      <ul>
      <#list difference.oldFeature.attributes as attribute>
        <li>${attribute.name}: ${attribute.value}
       </#list>
      </ul>
    </#if>
    </p>
  </#list>
  <#if !differenceFound>
    No differences found
  </#if>
</#list>
</body>
</html>