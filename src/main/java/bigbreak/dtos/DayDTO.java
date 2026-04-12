package bigbreak.dtos;

import java.util.List;
import java.util.Set;

public class DayDTO {
    private int dayNumber;
    private String time;
    private String move;
    private int moveCount;
    private String pageCount;
    private int pageCountEights;
    private int eightsWoMoves;
    private Set<String> locations;
    private List<SceneDTO> scenes;

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageCountEights() {
        return pageCountEights;
    }

    public void setPageCountEights(int pageCountEights) {
        this.pageCountEights = pageCountEights;
    }

    public int getEightsWoMoves() {
        return eightsWoMoves;
    }

    public void setEightsWoMoves(int eightsWoMoves) {
        this.eightsWoMoves = eightsWoMoves;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public void setLocations(Set<String> locations) {
        this.locations = locations;
    }

    public List<SceneDTO> getScenes() {
        return scenes;
    }

    public void setScenes(List<SceneDTO> scenes) {
        this.scenes = scenes;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}
