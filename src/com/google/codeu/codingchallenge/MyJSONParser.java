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
            throw new IOException("Invalid JSON-lite object.");
        }

        MyJSON obj = new MyJSON();

        Matcher m = Pattern.compile("(?:\\s)*( *\".*?\" *)(?:\\s)*:(?:\\s)*( *\\{\".*?\" *\\}| *\".*?\" *)(?:\\s)*").matcher(object);
        while (m.find()) {
            if (!validKeyValue(m.group())) {
                throw new IOException("Invalid key-value pair.");
            }
            String key = m.group(1);
            key = key.replaceAll("\\s+", " ").trim();
            String noQuotes = key.substring(1, key.length() - 1).trim();
            String value = m.group(2).trim();
            if (validString(value)) {
                obj.setString(noQuotes, value.substring(1, value.length() - 1));
            } else {
                value = value.replace("\\s+", " ").trim();
                obj.setObject(noQuotes, parse(value));
            }
        }
        return obj;
    }

    private boolean validString(String str) {
        String prev = "[\\s]*(\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\")[\\s]*";
        String test = "[\\s]*((?<!\\\\)\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\")[\\s]*";
        String test2 = "( )*\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*[\"\\s]*\"";
        String idk = "[\\s]*(\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\")[\\s]*";
        Pattern pat = Pattern.compile(test2);
        Matcher matcher = pat.matcher(str);
        return matcher.matches();
    }

    private boolean validKeyValue(String str) {
        String[] keyValue = str.split(":",2);
        return validString(keyValue[0]) && (validString(keyValue[1]) || validObject(keyValue[1]));
    }

    private boolean validObject(String s) {
        String valObj = "\\{(\\s|\\n)*}|\\{((((\\s|.)*\".*?\" *: *\".*?\",(\\s)*)+((\\s|.)*\".*?\" *: *\".*?\"(\\s)*)+)\\}|(((\\s|.)*\".*?\" *: *\".*?\"(\\s|.))))*(\\s)*\\}";
        Pattern obj = Pattern.compile(valObj);
        Matcher m = obj.matcher(s);
        return m.matches();
    }

    public static void main(String[] args) {
        MyJSONParser j = new MyJSONParser();
        String one = "abc";
        String two = "";
        String three =  "\"a\tb\n\bc\"";
        String four = "\\g";
        System.out.print(j.validString(one));
        System.out.print(j.validString(two));
        System.out.print(j.validString(three));
    }
}
