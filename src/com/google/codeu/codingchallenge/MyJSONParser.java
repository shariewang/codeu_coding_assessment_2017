// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.codingchallenge;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class MyJSONParser implements JSONParser {

    @Override
    public JSON parse(String object) throws IOException {
        if (!validObject(object)) {
            throw new IOException("Invalid JSON-lite object");
        }
        MyJSON obj = new MyJSON();
        //parse the string and create json map with all mappings (include nested objects)
        String keyValue = "( *\".*?\" *):( *\".*?\" *)";
        Pattern objRegex = Pattern.compile(keyValue);
        Matcher matcher = objRegex.matcher(object);
        while (matcher.find()) { //for each key-value match
            String key = matcher.group(1);
            String value = matcher.group(2);
            if (validString(value)) {
                obj.stringMap.put(key, value);
            } else {
                obj.objectMap.put(key, parse(value));
            }
        }
        return obj;
    }

    private boolean validString(String str) {
        String jsonRegex = "\".*\"";
        String test = "\"(((\"|\\t|\\n)*[a-z\\s]*)*|([a-z\\s]*(\"|\\t|\\n)*)*)*\"";
        Pattern pat = Pattern.compile("[\\s]*(\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\")[\\s]*");
        Matcher matcher = pat.matcher(str);
        return matcher.matches();
    }

    private boolean validKeyValue(String str) {
        String[] keyValue = str.split(":",2);
        return validString(keyValue[0]) && (validString(keyValue[1]) || validObject(keyValue[1]));
    }

    private boolean validObject(String s) {
        String valObj = "\\{(((\\s|.)*\".*?\" *: *\".*?\",(\\s)*)+((\\s|.)*\".*?\" *: *\".*?\"(\\s)*)+)\\}|(((\\s|.)*\".*?\" *: *\".*?\"(\\s|.)))\\}";
        Pattern obj = Pattern.compile(valObj);
        Matcher m = obj.matcher(s);
        if (!m.matches()) {
            return false;
        } else {
            //Check if the key-value pairs are valid
            String pair = "( *\".*?\" *: *\".*?\" *)";
            Pattern objRegex = Pattern.compile(pair);
            Matcher matcher = objRegex.matcher(s);
            while (matcher.find()) {
                if (!validKeyValue(matcher.group(1)))
                    return false;
            }
            return true;
        }
    }
}
