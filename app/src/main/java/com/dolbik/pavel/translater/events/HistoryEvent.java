package com.dolbik.pavel.translater.events;


import com.dolbik.pavel.translater.model.History;

public class HistoryEvent {

    
    public static class Click {
        private History history;
        public Click(History history) { this.history = history; }
        public History getHistory()   { return history; }
    }


    public static class UpdateFavoriteList {
        public UpdateFavoriteList() {}
    }


    public static class UpdateHistoryList {
        public UpdateHistoryList() {}
    }


    public static class IsHistoryListEmpty {
        private boolean isEmpty;
        public IsHistoryListEmpty(boolean isEmpty) { this.isEmpty = isEmpty; }
        public boolean isEmpty() { return isEmpty;}
    }


    public static class IsFavoriteListEmpty {
        private boolean isEmpty;
        public IsFavoriteListEmpty(boolean isEmpty) { this.isEmpty = isEmpty; }
        public boolean isEmpty() { return isEmpty;}
    }

}
