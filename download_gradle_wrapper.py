#!/usr/bin/env python3
import urllib.request
import base64
import sys

# Download gradle-wrapper.jar for Gradle 9.3.1
url = "https://services.gradle.org/distributions/gradle-9.3.1-wrapper.jar"
jar_path = "gradle-wrapper.jar"

try:
    print(f"Downloading gradle-wrapper.jar from {url}...")
    urllib.request.urlretrieve(url, jar_path)
    print(f"Successfully downloaded to {jar_path}")
    
    # Read and encode
    with open(jar_path, 'rb') as f:
        jar_data = f.read()
    
    encoded = base64.b64encode(jar_data).decode('ascii')
    print(f"File size: {len(jar_data)} bytes")
    print(f"Base64 encoded size: {len(encoded)} characters")
    
except Exception as e:
    print(f"Error: {e}", file=sys.stderr)
    sys.exit(1)
