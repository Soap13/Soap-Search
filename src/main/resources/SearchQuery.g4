grammar SearchQuery;

// 整个查询语句由多个 term 构成
searchQuery
 : searchTerm* EOF
 ;

searchTerm
 : SITE_INCLUDE
 | SITE_EXCLUDE
 | WORD
 ;

// 匹配包含 site: 但不以 - 开头的项 => 表示包含
//site: 开头后接一个或多个字母、数字、点、冒号、斜杠、减号或下划线的字符串，用于识别类似网站地址的标记。
SITE_INCLUDE
 : 'site:' [a-zA-Z0-9.:/\-_]+
 ;

// 匹配 -site: 开头的项 => 表示排除
SITE_EXCLUDE
 : '-'[a-zA-Z0-9.:/\-_]+
 ;

// 普通词
WORD
 : ~[ \t\r\n]+
 ;

// 空格忽略
WS
 : [ \t\r\n]+ -> skip
 ;

