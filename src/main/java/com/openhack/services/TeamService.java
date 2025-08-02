package com.openhack.services;

import com.openhack.Response.HackathonReportResponse;
import com.openhack.Response.HackathonResponse;
import com.openhack.Response.TeamResponse;
import com.openhack.controller.EmailActivationLink;
import com.openhack.dao.HackathonDao;
import com.openhack.dao.TeamDao;
import com.openhack.dao.TeamMemberDao;
import com.openhack.dao.UserDao;
import com.openhack.model.Hackathon;
import com.openhack.model.Team;
import com.openhack.model.TeamMember;
import com.openhack.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired
    private TeamDao teamDao;

    @Autowired
    private HackathonDao hackathonDao;

    @Autowired
    private TeamMemberDao teamMemberDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailActivationLink emailActivationLink;

    @Transactional
    public ResponseEntity<?> createTeam(Long hid){
        Hackathon hackathon = hackathonDao.findItemById(Optional.ofNullable(hid).orElse(-1L));

        Team team = new Team();
        team.setHackathon(hackathon);

        teamDao.createItem(team);

        TeamResponse teamResponse = new TeamResponse(team);

        return ResponseEntity.ok().body(teamResponse);
    }

    @Transactional
    public ResponseEntity<?> registerTeam(Long hid,
                                          String team_name,
                                          String leader_screenname,
                                          String leader_rold,
                                          String member2_screenname,
                                          String member2_role,
                                          String member3_screenname,
                                          String member3_role,
                                          String member4_screenname,
                                          String member4_email) {

        long hackid = Optional.ofNullable(hid).orElse(-1L);
        Hackathon hackathon = hackathonDao.findItemById(hackid);

        if(!hackathon.getStatus().equals("created"))
            return ResponseEntity.badRequest().body("This hackathon is in progress/completed.");

        String [] screennames = new String[]{leader_screenname,member2_screenname,member3_screenname,member4_screenname};

        Team checkTeam = teamDao.findItemByName(team_name);
        if(checkTeam != null){
            return ResponseEntity.badRequest().body("This team is not available for use");
        }


        List<String> list_screennames = Arrays.asList(screennames);

        List<Team> teams = teamDao.findTeams();
        if(teams != null){
            for (Team team:
                 teams) {
                if(team.getHackathon().getHid() == hid) {
                    List<TeamMember> teamMembers = team.getTeamMembers();
                    for (TeamMember teamMember :
                            teamMembers) {
                        User user = userDao.findById((long) teamMember.getMember_id());
                        if (list_screennames.contains(user.getScreenName())) {
                            return ResponseEntity.badRequest().body("User " + user.getName() + " has registered for hackathon "+team.getHackathon().getName()+" already");
                        }
                    }
                }
            }
        }


        User user = userDao.findByScreenname(leader_screenname);
        User user2=null;
        User user3=null;
        User user4=null;

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = new SimpleDateFormat("yyyy-MM-dd").parse(hackathon.getStart_date());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String s2 = simpleDateFormat.format(new Date());
            date2 = new SimpleDateFormat("yyy-MM-dd").parse(s2);

            if (date2.compareTo(date1) > 0) {
                return ResponseEntity.badRequest().body("Hackathon already started");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Cannot parse date exception");
        }


        Team team = new Team();
        team.setHackathon(hackathon);
        team.setTeam_name(team_name);

        String owner = hackathon.getOwner().getName();
        String [] judges = hackathon.getJudge_screenname().split("\\$");


        long tid = team.getTid();
        String id = ""+Long.toString(tid)+"_"+Long.toString(hackid);

        TeamMember teamMember1 = new TeamMember();
        teamMember1.setTeam(team);
        teamMember1.setMember_id((int)user.getUid());
        teamMember1.setP_status("None");


        //System.out.println(member2_screenname);
        //System.out.println(member2_screenname.equals("undefined"));
        
        TeamMember teamMember2 = null ;
        if(member2_screenname != null && !member2_screenname.equals("undefined")) {

            if(member2_screenname.equals(owner))
                return ResponseEntity.badRequest().body(owner+" is the owner. Owner cannot register for his hackathon");

            if(Arrays.asList(judges).contains(member2_screenname))
                return ResponseEntity.badRequest().body(member2_screenname+" is a judge. A judge cannot be registerd fot the same hackathon");

            user2 = userDao.findByScreenname(member2_screenname);
            teamMember2 = new TeamMember();

            if(user2 != null ) {
                teamMember2.setMember_id((int) user2.getUid());
                teamMember2.setTeam(team);
                teamMember2.setP_status("None");
            }
            else{
                return ResponseEntity.badRequest().body(member2_screenname+" is not a valid member in the system.");
            }

        }


        TeamMember teamMember3 = null;
        if(member3_screenname != null && !member3_screenname.equals("undefined")) {

            if(member3_screenname.equals(owner))
                return ResponseEntity.badRequest().body(owner+" is the owner. Owner cannot register for his hackathon");

            if(Arrays.asList(judges).contains(member3_screenname))
                return ResponseEntity.badRequest().body(member3_screenname+" is a judge. A judge cannot be registerd fot the same hackathon");

            user3 = userDao.findByScreenname(member3_screenname);
            teamMember3 = new TeamMember();

            if(user3 != null) {
                teamMember3.setMember_id((int) user3.getUid());
                teamMember3.setTeam(team);
                teamMember3.setP_status("None");

            }else{
                return ResponseEntity.badRequest().body(member3_screenname+" is not a valid member in the system.");
            }
        }


        TeamMember teamMember4 = null;
        if(member4_screenname != null && !member4_screenname.equals("undefined")) {

            if(member4_screenname.equals(owner))
                return ResponseEntity.badRequest().body(owner+" is the owner. Owner cannot register for his hackathon");

            if(Arrays.asList(judges).contains(member4_screenname))
                return ResponseEntity.badRequest().body(member4_screenname+" is a judge. A judge cannot be registerd fot the same hackathon");

            user4 = userDao.findByScreenname(member4_screenname);
            teamMember4 = new TeamMember();

            if(user4 != null) {
                teamMember4.setMember_id((int) user4.getUid());
                teamMember4.setTeam(team);
                teamMember4.setP_status("None");

            }
            else{
                return ResponseEntity.badRequest().body(member4_screenname+" is not a valid member in the system.");
            }
        }

        teamDao.createItem(team);
        teamMemberDao.createItem(teamMember1);
        new Thread(() -> {
            System.out.println("Sending email to "+user.getEmail());
            emailActivationLink.sendPaymentLink(user.getEmail(),hackathon.getHid(),user.getScreenName());
        }).start();

        if(teamMember2 != null){
            teamMemberDao.createItem(teamMember2);
            String screenname = user2.getScreenName();
            String email = user2.getEmail();
            new Thread(() -> {
                System.out.println("Sending email to "+user.getEmail());
                emailActivationLink.sendPaymentLink(email,hackathon.getHid(),screenname);
            }).start();
        }
        if(teamMember3 != null){
            teamMemberDao.createItem(teamMember3);
            String screenname = user3.getScreenName();
            String email = user3.getEmail();
            new Thread(() -> {
                System.out.println("Sending email to "+user.getEmail());
                emailActivationLink.sendPaymentLink(email,hackathon.getHid(),screenname);
            }).start();
        }
        if(teamMember4 != null){
            teamMemberDao.createItem(teamMember4);
            String screenname = user4.getScreenName();
            String email = user3.getEmail();
            new Thread(() -> {
                System.out.println("Sending email to "+user.getEmail());
                emailActivationLink.sendPaymentLink(email,hackathon.getHid(),screenname);
            }).start();
        }

        Team updated_team = teamDao.findById(team.getTid());

        TeamResponse teamResponse = new TeamResponse(updated_team);

        return ResponseEntity.ok().body(teamResponse);

    }

    @Transactional
    public ResponseEntity<?> updateTeamScore(long tid,
                                             float score){



        Team team = teamDao.findById(tid);
        if(team != null){

            Hackathon hackathon = team.getHackathon();
            if(hackathon.getStatus().equals("opened"))
                return ResponseEntity.badRequest().body("Admin has not opened hackthon for grading yet.");
            else if(hackathon.getStatus().equals("final"))
                return ResponseEntity.badRequest().body("This hackathon is closed for grading.");

            team.setScore(score);
        }

        return ResponseEntity.ok().body("Score submitted");

    }

    @Transactional
    public ResponseEntity<?> getTeams(String hackName){
        Hackathon hackathon = hackathonDao.findItemByName(hackName);
        List<Team> allTeams = teamDao.findTeams();
        List<TeamResponse> hackTeams = new ArrayList<>();

        if(allTeams != null) {
            for (Team team :
                    allTeams) {
                if (team.getHackathon().getHid() == hackathon.getHid()) {
                    TeamResponse teamResponse = new TeamResponse(team);
                    hackTeams.add(teamResponse);
                }
            }
        }
        return ResponseEntity.ok().body(hackTeams);

    }

    @Transactional
    public ResponseEntity<?> getFinalTeams(String hackName){
        Hackathon hackathon = hackathonDao.findItemByName(hackName);
        List<Team> allTeams = teamDao.findTeams();
        List<HackathonReportResponse> hackathonReportResponses = new ArrayList<>();

        if(allTeams != null){
            for (Team team:
                 allTeams) {
                if(team.getHackathon().getHid() == hackathon.getHid()){

                    List<String> member_names = new ArrayList<>();
                    List<TeamMember> teamMembers = team.getTeamMembers();

                    for(TeamMember teamMember : teamMembers){
                        User user = userDao.findById((long)teamMember.getMember_id());
                        member_names.add(user.getScreenName());
                    }

                    HackathonReportResponse hackathonReportResponse = new HackathonReportResponse(team.getTid(),member_names,team.getScore());
                    hackathonReportResponses.add(hackathonReportResponse);
                }
            }
        }

        List<HackathonReportResponse> sortedTeam = hackathonReportResponses.stream().sorted(Comparator.comparingDouble(HackathonReportResponse::getScore)).collect(Collectors.toList());

        return ResponseEntity.ok().body(sortedTeam);
    }

    @Transactional
    public ResponseEntity<?> emailTeamMembers(long hid){
        Hackathon hackathon = hackathonDao.findItemById(hid);
        List<Team> allTeams = teamDao.findTeams();


        if(allTeams != null){
            for (Team team:
                    allTeams) {

                List<TeamMember> teamMembers = team.getTeamMembers();
                for(TeamMember teamMember : teamMembers){
                    User user = userDao.findById((long)teamMember.getMember_id());

                    new Thread(() -> {
                        System.out.println("Sending email to "+user.getEmail());
                        emailActivationLink.sendHackFinalNotification(user.getEmail(), hackathon.getName());
                    }).start();

                }

            }
        }

        return ResponseEntity.ok().body("Email sent");

    }

}
