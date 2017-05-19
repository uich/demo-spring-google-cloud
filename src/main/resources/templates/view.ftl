<html>
<head>
<style type="text/css">
</style>
</head>
<body>
<h1>解析結果</h1>
<img src="${imageUrl}" width="30%">
<h2>タグ</h2>
<ul>
<#list imageInfo.labels as label>
<li>${label.text}(score:${label.score})
</#list>
</ul>
<h2>カラー</h2>
<#list imageInfo.colors as color>
<div style="background-color : ${color.rgb.htmlRgb()}">
<font color="${color.rgb.opposite().htmlRgb()}">score:${color.score}</font>
</div>
</#list>
</body>
</html>