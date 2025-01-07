<div align="center">
    <h1>Notion to Markdown</h1>
    <p>
        <b>A simple and easy-to-use Java library for converting notion to markdown</b>
    </p>
    <p align="center">
        <a href="https://developers.notion.com/reference/intro"><img src="https://img.shields.io/badge/Notion-Developer-blue?logo=Safari" alt="Website"/></a>
    </p>
    <p align="center">
    <a href="#About">About</a> •
    <a href="#Integration">Integration</a> •
    <a href="#Usage">Usage</a> •
    <a href="#Acknowledgements">Acknowledgements</a>
</p>  
</div>

## About

This project aims to convert Notion content into Markdown, which is simple and easy to use.

It also provides some methods for processing notion objects.

## Integration

For Gradle, add the following dependency to your `build.gradle.kts` file:

```kotlin
dependencies { 
    // add the library
    implementation("io.github.oops418:notion-to-md:1.0.0")

    // a choice of logging implementation
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.3")
    
    // or
    implementation("org.slf4j:slf4j-simple:2.0.16")
    
    // or...
}
```

> If you are using other build tools, you can find other formats on the [Maven Central Repository - notion to md](https://central.sonatype.com/artifact/io.github.oops418/notion-to-md).

This Project is using `slf4j2` as the logging facade, so you can use any logging implementation you like.

If you don't introduce any logging implementation, it will use stdout as the default logging implementation.

---

## Usage

```java
// Create a MarkdownConverter instance
MarkdownConverter converter = MarkdownConverter.getInstance(YourNotionApiSecret);

// Get all supported mdBlocks from a Notion page
List<MdBlocks> mdBlocks = converter.pageToMarkdownBlocks(pageId);

// Convert the mdBlocks to a markdown string
converter.toMarkdownString(mdBlocks);
```

The `adaptor.notion.utils.NotionUtils` module provides several helpful utility functions for markdown processing, enabling efficient manipulation of Notion objects.

---

## Acknowledgements

Thanks to the following projects for their inspiration and guidance:

1. [notion-sdk-jvm](https://github.com/seratch/notion-sdk-jvm) - A simple and easy to use client for the Notion API

2. [notion-to-md](https://github.com/souvikinator/notion-to-md) - A Node.js package that allows you to convert Notion pages to Markdown format
