***

# 📘 Scoped Values (Java 25)

## 🚀 Overview

This module demonstrates **Scoped Values**, finalized in **Java 25** via **JEP 506**, which provide a **safe, immutable, lexically-scoped** alternative to `ThreadLocal`. Scoped Values are designed to work seamlessly with **Virtual Threads** and **Structured Concurrency**, offering predictable and efficient context propagation.

According to the OpenJDK proposal, Scoped Values allow sharing of immutable data across a thread and its child threads, making them easier to reason about, safer, and more performant than thread-local variables.   
Multiple expert analyses highlight that Scoped Values eliminate the mutability issues, memory leaks, and context‑bleed problems typical of `ThreadLocal`, especially under heavy virtual‑thread workloads. [\[oracle.com\]](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) [\[houseofbrick.com\]](https://houseofbrick.com/blog/java-versions-update/), [\[openjdk.org\]](https://openjdk.org/)

***

## 🎯 Learning Objectives

By completing this tut, you will:

*   Understand why **ThreadLocal is problematic** with Virtual Threads
*   Learn how **Scoped Values** solve these issues
*   Use them with **StructuredTaskScope** (Java 25 preview) for clean task scoping
*   Build an **HTTP server** using Virtual Threads + Scoped Values
*   Verify correct context propagation using a **load client**

***

## 🧠 Why ThreadLocal Is Deprecated-in-Spirit (Not API)

ThreadLocal’s design assumptions (few long‑lived threads, manual cleanup) no longer hold with Virtual Threads:

*   ❌ Values can leak across reused threads
*   ❌ Mutable global state is hard to reason about
*   ❌ InheritableThreadLocal becomes extremely expensive
*   ❌ Context bleed is common in frameworks
*   ❌ Hard‑to‑debug behavior under high concurrency

Modern analyses point out these pitfalls clearly. [\[jdk.java.net\]](https://jdk.java.net/26/)

***

## 🌟 What Scoped Values Fix

Scoped Values:

*   ✔ Provide immutable, block‑scoped data
*   ✔ Automatically clean themselves when scope exits
*   ✔ Propagate safely to child virtual threads
*   ✔ Reasonable lifetime tied to lexical structure
*   ✔ Perfect for Virtual Threads + Structured Concurrency  
    (Oracle documentation explains StructuredTaskScope as a preview feature in Java 25 requiring explicit enabling of previews.) [\[lucanerlich.com\]](https://lucanerlich.com/java/modern-java-features/)

***

## 📂 Included Examples

All code lives under:

    src/main/java/com/ppp/

### 1) **ScopedValueBasic**

Shows how to:

*   Bind a ScopedValue using `ScopedValue.where(...).run(...)`
*   Access values inside the lexical scope
*   Demonstrate that values are unavailable outside scope

### 2) **RequestContextDemo**

Demonstrates request‑scoped data such as:

*   `REQUEST_ID`
*   Logging context
*   Propagation into multiple child tasks

### 3) **ScopedValueHttpServer**

A production‑style Virtual‑Thread HTTP server that:

*   Uses Scoped Values to assign and propagate a unique `requestId`
*   Demonstrates propagation across **multiple parallel subtasks** using  
    `StructuredTaskScope.open().fork(...)`
*   Runs on **<http://localhost:8081/api>**

### 4) **ScopedValueHttpLoad**

A Virtual‑Thread load generator that:

*   Sends thousands of concurrent HTTP requests
*   Validates:
    *   Server correctness
    *   Per‑request unique ScopedValue
    *   No context bleeding
*   Prints meaningful exceptions (expanded logging)

***

## 🧪 Testing Instructions

### ✔ Start the server

Run `ScopedValueHttpServer`.

Visit:

    http://localhost:8081/api

Expected output contains:

*   `requestId` (unique UUID)
*   `user@requestId`
*   `items@requestId`

### ✔ Run the load client

Run `ScopedValueHttpLoad`.

Validate:

*   Throughput
*   Stability
*   No cross‑request contamination
*   REQ\_ID is always unique per request and correctly propagated across subtasks

***

## 🎥 Using JFR (Optional but Recommended)

### Why JFR?

JFR reveals:

*   Virtual Thread lifecycle events
*   Subtask execution
*   Structured concurrency scheduling
*   I/O blocking
*   Context propagation behavior  
    (Oracle documentation highlights that JFR captures thread interactions, blocking events, and execution states. )

### Steps:

1.  Run server with JFR enabled (if using your JfrStarter).
2.  Open `.jfr` file in **Java Mission Control** (external tool).
3.  Analyze:
    *   VirtualThreadStart/End
    *   SocketRead/Write durations
    *   Thread state transitions

***

## 🔧 IntelliJ Configuration

### 1) Use **JDK 25**

### 2) Enable preview features

Add to Run Configuration VM options:

    --enable-preview

(Structured Concurrency is a preview feature as documented by Oracle.) [\[lucanerlich.com\]](https://lucanerlich.com/java/modern-java-features/)

***

## 📘 References

*   Scoped Values finalized for Java 25 via **JEP 506**, offering safer immutable data propagation. [\[oracle.com\]](https://www.oracle.com/java/technologies/java-se-support-roadmap.html)
*   Scoped Values fix ThreadLocal issues in concurrent systems (immutability, cleanup, propagation). [\[houseofbrick.com\]](https://houseofbrick.com/blog/java-versions-update/), [\[openjdk.org\]](https://openjdk.org/)
*   ThreadLocal pitfalls become severe with modern Java concurrency. [\[jdk.java.net\]](https://jdk.java.net/26/)
*   Structured Concurrency in Java 25 uses `StructuredTaskScope.open()` (preview feature). [\[lucanerlich.com\]](https://lucanerlich.com/java/modern-java-features/)

***
