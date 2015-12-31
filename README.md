# Resting-Place -- testing Java HTTP client ideas for CouchDB

This repository contains a simple experiment in using delegates to receive
the results of HTTP requests made to CouchDB. Right now only one call is
partially implemented, GET a document, to show the ideas.

The main interesting points are:

* Each request is represented as an operation with is a template for a HTTP request.
* The way the client receives a response (stream, string or object) is specified by
    using different response delegate interfaces rather than by using different
    method calls.
* The `Client` and `Database` objects, which typically have a lot of logic, have very
    little logic; it's all in the operation objects.
