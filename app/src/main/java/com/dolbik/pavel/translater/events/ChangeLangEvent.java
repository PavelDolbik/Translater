package com.dolbik.pavel.translater.events;

import com.dolbik.pavel.translater.model.Language;

public class ChangeLangEvent {


    public static class From {
        private Language language;
        public From(Language language) { this.language = language; }
        public Language getLanguage()  { return language; }
    }


    public static class To {
        private Language language;
        public To(Language language)  { this.language = language; }
        public Language getLanguage() { return language; }
    }

}
