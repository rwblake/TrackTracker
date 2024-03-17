package team.bham.service.spotify;

public class MyInsightsHTTPResponse {

    /** This class is used to temporarily store data for a Insights HTTP response.
     * It is converted to JSON before being sent.
     */

    MyInsightsGraphData graphData;

    public MyInsightsHTTPResponse(MyInsightsGraphData graphData) {
        this.graphData = graphData;
    }
}
