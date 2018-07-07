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

package com.fredboat.backend.shared;

import java.util.Optional;

/**
 * Created by napster on 21.03.18.
 */
public enum Language {
    EN_US("en", "US"),
    AF_ZA("af", "ZA"),
    AR_SA("ar", "SA"),
    BG_BG("bg", "BG"),
    CA_ES("ca", "ES"),
    ZH_CN("zh", "CN"),
    ZH_TW("zh", "TW"),
    HR_HR("hr", "HR"),
    CS_CZ("cs", "CZ"),
    DA_DK("da", "DK"),
    NL_NL("nl", "NL"),
    FI_FI("fi", "FI"),
    FIL_PH("fil", "PH"),
    FR_FR("fr", "FR"),
    DE_DE("de", "DE"),
    EL_GR("el", "GR"),
    HE_IL("he", "IL"),
    HU_HU("hu", "HU"),
    ID_ID("id", "ID"),
    IT_IT("it", "IT"),
    JA_JP("ja", "JP"),
    KO_KR("ko", "KR"),
    MS_MY("ms", "MY"),
    NO_NO("no", "NO"),
    EN_PT("en", "PT"),
    PL_PL("pl", "PL"),
    PT_PT("pt", "PT"),
    PT_BR("pt", "BR"),
    RO_RO("ro", "RO"),
    RU_RU("ru", "RU"),
    ES_ES("es", "ES"),
    SV_SE("sv", "SE"),
    EN_TS("en", "TS"),
    TR_TR("tr", "TR"),
    TH_TH("th", "TH"),
    VI_VN("vi", "VN"),
    CY_GB("cy", "GB"),
    //
    ;

    private final String code;

    Language(String language, String country) {
        this.code = language + "_" + country;
    }

    public String getCode() {
        return this.code;
    }

    /**
     * This method tries to parse an input into a language that we recognize.
     *
     * @param input
     *         input to be parsed into a Language known to us (= defined in this enum)
     *
     * @return the optional language identified from the input.
     */
    public static Optional<Language> parse(String input) {
        for (Language language : Language.values()) {
            if (language.name().equalsIgnoreCase(input)) {
                return Optional.of(language);
            }
        }

        return Optional.empty();
    }
}
