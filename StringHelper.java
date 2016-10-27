import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Created by mr_robot on 8/27/2016.
 */
class StringHelper {

    public static String formatAsPhoneNumber(String phoneNumber){
        String formattedString = phoneNumber;
        ArrayList<Character> characters = new ArrayList<>();

        for(char c : formattedString.toCharArray()){
            if (Character.isDigit(c)) {
                characters.add(c);
            }
        }

        if(characters.size() != 10){
            System.out.println("Error: Unable to format string " +
                    "to phone formtat (###) ###-####.  \n(Digits required: " +
                    "10, provided: " + characters.size() + ").");
            return null;
        }else{
            formattedString = "";
            for(Character c : characters){
                formattedString += c;
            }
            formattedString = String.format("(%s) %s-%s", formattedString.substring(0, 3),
                    formattedString.substring(3, 6), formattedString.substring(6, 10));
        }
        return formattedString;
    }

    public static String generateComment(Inspection i){
        String comment = "";
        int rand0 = 1 + (int)(Math.random() * 5);
        int rand1 = 1 + (int)(Math.random() * 5);
        int rand2 = 1 + (int)(Math.random() * 5);
        int rand3 = 1 + (int)(Math.random() * 5);

        String determinedBy = i.getDeterminedBy().toLowerCase();
        String type = i.getType().stringName.toLowerCase();
        String occupancy = i.getOccupancy().toLowerCase();
        String determination = i.getDeterminedBy().toLowerCase();

        if(determinedBy.contains("direct") && type.contains("call")){
            switch(rand0){
                case 1:
                    comment += "Call-back card left with ";
                    break;
                case 2:
                    comment += "Made contact and left a Call-back card with ";
                    break;
                case 3:
                    comment += "A call-back card was handed to ";
                    break;
                case 4:
                    comment += "The cb card was given to ";
                    break;
                default:
                    comment += "Call-back card was accepted by ";
                    break;
            }
        }else{
            switch(rand0){
                case 1:
                    comment += "Property occupied by ";
                    break;
                case 2:
                    comment += "Occupied by ";
                    break;
                default:
                    comment += "The property is occupied by ";
                    break;
            }
        }
        if(determination.contains("direct")){
            if (occupancy.contains("unknown")){
                switch (rand1) {
                    case 1:
                        comment += "an unknown occupant.  ";
                        break;
                    case 2:
                        comment += "an unknown occupant at the property.  ";
                        break;
                    case 3:
                        comment += "an unknown person occupying the property.  ";
                        break;
                    case 4:
                        comment += "an occupant who wished not to disclose his identity.  ";
                        break;
                    default:
                        comment += "an occupant who wished not to disclose her identity.  ";
                        break;
                }
            }else if(occupancy.contains("owner")) {
                switch(rand1){
                    case 1:
                        comment += "an occupant claiming to be a relative of the mortgagor.  ";
                        break;
                    case 2:
                        comment += "person who said she was a relative of the owner.  ";
                        break;
                    case 3:
                        comment += "someone who claimed to be a relative of the mortgagor.  ";
                        break;
                    case 4:
                        comment += "an occupant who confirmed the owner lives at the property.  ";
                        break;
                    default:
                        comment += "an individual claiming to be related to the owner occupying the residence.  ";
                        break;
                }
            }
            switch(rand2){
                case 1:
                    comment += "No other information was disclosed.  ";
                    break;
                case 2:
                    comment += "Refused to disclose additional information and I was asked to leave.  ";
                    break;
                case 3:
                    comment += "Additional information was not provided.  ";
                    break;
                case 4:
                    comment += "No other details were provided and I was asked to leave the property.  ";
                    break;
                default:
                    comment += "Occupant declined to provide any additional info.  ";
                    break;
            }
        }else if(determination.contains("visual") || determination.contains("neighbor")){
            switch(rand1) {
                case 1:
                    comment += "an unknown occupant.  Knocked on door, but nobody was home.  ";
                    break;
                case 2:
                    comment += "an unknown occupant.  Approached property, but no answer at door.  ";
                    break;
                case 3:
                    comment += "an unknown occupant.  No answer at door.  ";
                    break;
                case 4:
                    comment += "an unknown occupant.  Nobody answered when I approached the property.  ";
                    break;
                default:
                    comment += "an unknown occupant.  No one answered after knocking on the door.  ";
                    break;
            }
        }if(determination.contains("visual")) {
            switch (rand2) {
                case 1:
                    comment += "Unable to make contact with the neighbor.  ";
                    break;
                case 2:
                    comment += "Unable to verify with neighbors.  ";
                    break;
                case 3:
                    comment += "Neighbors could not confirm occupancy.  ";
                    break;
                case 4:
                    comment += "Neighbors did not provide any info.  ";
                    break;
                default:
                    comment += "Unable to speak with neighbors.  ";
                    break;
            }
            comment += generateOccupancyIndicatorComment(i);
        }
        if(determination.contains("neighbor")){
            switch(rand3){
                case 1:
                    comment += "Neighbors confirmed that the property is occupied.  ";
                    break;
                case 2:
                    comment += "Next door neighbors claimed that the property is occupied.  ";
                    break;
                case 3:
                    comment += "Neighbors across the street believe the property is occupied.  ";
                    break;
                case 4:
                    comment += "Neighbors down the road confirmed that this property is occupied.  ";
                    break;
                default:
                    comment += "The neighbors believe this property to be occupied.  ";
                    break;
            }
        }
        return comment;
    }

    private static String generateOccupancyIndicatorComment(Inspection inspection){
        StringBuilder beginningSentence = new StringBuilder("");
        StringBuilder endSenetence = new StringBuilder("");
        ObservableList<String> indicators = inspection.getOccupancyIndicatorList();
        int num = indicators.size();
        int rand = 1 + (int)(Math.random() * 5);
        String pluralOpt;
        switch(rand){
            case 1:
                pluralOpt = (num > 1) ? "indicate" : "indicates";
                endSenetence.append(" ").append(pluralOpt).append(" occupancy.  ");
                break;
            case 2:
                pluralOpt = (num > 1) ? "suggest" : "suggests";
                endSenetence.append(" ").append(pluralOpt).append(" the property is occupied.  ");
                break;
            case 3:
                pluralOpt = (num > 1) ? "were" : "was";
                endSenetence.append(" ").append(pluralOpt).append(" seen at the property.  ");
                break;
            case 4:
                pluralOpt = (num > 1) ? "imply" : "implies";
                endSenetence.append(" ").append(pluralOpt).append(" that this property is likely occupied.  ");
                break;
            default:
                pluralOpt = (num > 1) ? "confirm" : "confirms";
                endSenetence.append(" ").append(pluralOpt).append(" the property is occupied.  ");
                break;
        }

        if (indicators != null && num > 0) {
            beginningSentence.append(indicators.get(0));
            if (num == 1)
                return beginningSentence.append(endSenetence).toString();
            if (num == 2)
                return beginningSentence + " and " +
                        indicators.get(1).toLowerCase() + endSenetence;
            for (int i = 1; i < num; i++) {
                if (i == (num - 1))
                    beginningSentence.append(" and ");
                else
                    beginningSentence.append(", ");
                beginningSentence.append(indicators.get(i).toLowerCase());
            }
        }
        return beginningSentence.append(endSenetence).toString();
    }
}
