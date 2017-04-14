package com.dolbik.pavel.translater.events;


import com.dolbik.pavel.translater.model.History;

public class HistoryEvent {

    public static class Click {
        private History history;
        public Click(History history) { this.history = history; }
        public History getHistory()   { return history; }
    }
}
