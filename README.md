# 常用小工具

使用 Java, Python 等语言实现

<!-- @import "[TOC]" {cmd="toc" depthFrom=2 depthTo=2 orderedList=false} -->

<!-- code_chunk_output -->

- [1. dhtml2text](#1-dhtml2text)
- [2. listAllFiles](#2-listallfiles)
- [3. MenuGenerator](#3-menugenerator)
- [4. LinkCovertTool](#4-linkcoverttool)
- [5. Trello 导出的 JSON 文件导入到 Logseq](#5-trello-导出的-json-文件导入到-logseq)

<!-- /code_chunk_output -->



## 1. dhtml2text

该脚本可以下载指定页面下的所有a标签对应的链接，也可把下载下来的html页面合并为纯文本文件。

![dhtml2text](./imgs/dhtml2text-01.png)

脚本使用Python3, 网页转文本使用`html2text`完成。

使用:


### 安装依赖

```
pip3 install html2text
pip3 install chardet
```

### 运行

```
python3 dhtml2text.py
```

## 2. listAllFiles

Node.js，列出当前目录下的所有文件，并读取内容。

## 3. MenuGenerator

Node.js，根据OpenAPI规范，可提取Swagger生成的api.json文件中的关键字并写入文件。

## 4. LinkCovertTool

Java，可把后缀为`.desktop`，`.webloc`的多个网页快捷文件提取到链接并输出到Markdown文件。

## 5. Trello 导出的 JSON 文件导入到 Logseq

支持把从 Trello 看板导出的 JSON 文件导入到 Logseq

## 致谢

* SimpleHTTPServerWithUpload 来自 https://gist.github.com/UniIsland/3346170 和其他参与修改的人们

