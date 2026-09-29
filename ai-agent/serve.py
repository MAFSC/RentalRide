#!/usr/bin/env python3
"""Static server with correct MIME types for .js and .css files."""

from http.server import SimpleHTTPRequestHandler, HTTPServer

# Fix MIME types for .js and .css
SimpleHTTPRequestHandler.extensions_map['.js'] = 'application/javascript'
SimpleHTTPRequestHandler.extensions_map['.mjs'] = 'application/javascript'
SimpleHTTPRequestHandler.extensions_map['.css'] = 'text/css'
SimpleHTTPRequestHandler.extensions_map['.json'] = 'application/json'

if __name__ == '__main__':
    server = HTTPServer(('0.0.0.0', 8080), SimpleHTTPRequestHandler)
    print("Serving on http://0.0.0.0:8080")
    server.serve_forever()
