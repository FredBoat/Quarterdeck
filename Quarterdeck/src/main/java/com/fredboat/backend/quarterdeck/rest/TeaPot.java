/*
 * MIT License
 *
 * Copyright (c) 2016-2018 The FredBoat Org https://github.com/FredBoat/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.fredboat.backend.quarterdeck.rest;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by napster on 12.03.18.
 * <p>
 * Basic teapot implementation.
 * <p>
 * Ships have a historical responsibility to serve tea.
 */
@RestController
@SuppressWarnings("unused")
public class TeaPot {

    private final AtomicInteger teasServed = new AtomicInteger(0);

    @GetMapping("/brew")
    public ResponseEntity<Tea> brewTea(@Param("type") String type) {
        if (type.equalsIgnoreCase("coffee")) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
        }

        Tea.TeaType teaType;
        try {
            teaType = Tea.TeaType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new Tea(teaType, this.teasServed.incrementAndGet()), HttpStatus.OK);
    }

    private static class Tea {
        //http://theteaspot.com/about-tea.html
        enum TeaType {
            WHITE,
            GREEN,
            OOLONG,
            BLACK,
            PUERH,
            YERBA_MATE,
            HERBAL,
            ROOIBOS,
        }

        private final TeaType teaType;
        private final int number;

        public Tea(TeaType teaType, int number) {
            this.teaType = teaType;
            this.number = number;
        }

        public TeaType getTeaType() {
            return this.teaType;
        }

        public int getNumber() {
            return this.number;
        }
    }
}
