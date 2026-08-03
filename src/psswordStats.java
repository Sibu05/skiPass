import java.util.*;

public class psswordStats {
    
    public record Stat(String text, String source){}

    public static final List<Stat> STATS = List.of(
        new Stat("More than a quarter (28%) of cybersecurity professionals say employees using the same or weak passwords is their worst habit.", "Huntress Remote and Hybrid Cybersecurity Report"),
        new Stat("About 42% of people who have been hacked have passwords combining letters and numbers with personal significance, making them easier to guess.", "Forbes Advisor"),
        new Stat("Infostealers were used in 24% of cyber incidents in 2024.", "Huntress 2025 Cyber Threat Report"),
        new Stat("In 2023, 46% of respondents said they chose an easy-to-remember password over a more secure one.", "Pew Research Center"),
        new Stat("Only about 13% of people use random password generators to create their passwords.", "Forbes Advisor"),
        new Stat("Nearly 63% of people only change their passwords when prompted to.", "GoodFirms"),
        new Stat("34% of people create passwords by adding a number or special character to satisfy platform requirements.", "Forbes Advisor"),
        new Stat("About 8% of people admitted to sharing passwords with family or friends.", "Forbes Advisor"),
        new Stat("Nearly half (46%) of people had a password stolen in 2024.", "Forbes Advisor"),
        new Stat("35% of people who were hacked blame weak passwords.", "Forbes Advisor"),
        new Stat("53% of Americans say they're confident they know what to do in a cyberattack.", "Pew Research Center"),
        new Stat("90% of healthcare cybersecurity experts rate themselves very (37%) or somewhat (53%) confident they're prepared for a cyberattack.", "Huntress Healthcare Cybersecurity Report"),
        new Stat("Company data breaches caused 27% of stolen passwords.", "Forbes Advisor"),
        new Stat("More than 24 billion credentials are exposed each year in data breaches.", "Security Magazine"),
        new Stat("About 17% of people have no idea how their passwords were stolen.", "Forbes Advisor"),
        new Stat("The most commonly stolen info in password attacks: first/last name (39%), phone number (38%), personal address (37%).", "Forbes Advisor"),
        new Stat("About 23% of people reuse a password across three or four different accounts.", "Forbes Advisor"),
        new Stat("69% of people feel overwhelmed by the number of passwords they need to remember.", "Pew Research Center"),
        new Stat("30% of people had their passwords stolen because they'd reused it.", "Forbes Advisor"),
        new Stat("92% of IT professionals have admitted to reusing passwords.", "Bitwarden"),
        new Stat("The password management industry is expected to reach more than $2 billion in revenue by the end of 2025.", "Statista"),
        new Stat("Nearly 1 in 4 people (22%) don't use any particular method to keep their passwords safe.", "Forbes Advisor"),
        new Stat("27% of people feel they waste a lot of time managing passwords, while 67% feel it's not a waste of time.", "GoodFirms"),
        new Stat("38% of people write down their passwords.", "Forbes Advisor"),
        new Stat("41% of US adults write their passwords down, 34% save them to their browser, and 21% just reset them each time.", "Pew Research Center"),
        new Stat("35% of people rely only on memory to recall passwords.", "Forbes Advisor"),
        new Stat("51% of respondents in another study said they had their passwords memorized.", "Security.org"),
        new Stat("About 24% of people store their passwords on their computer.", "Forbes Advisor"),
        new Stat("The share of people using a password manager rose from 20% in 2019 to 32% in 2023.", "Pew Research Center"),
        new Stat("Google Password Manager is the most popular password manager choice.", "Security.org"),
        new Stat("About 68% of people are forced to change their passwords at least once a year due to compromised credentials.", "Forbes Advisor"),
        new Stat("A secure password should be long, random, and unique.", "CISA"),
        new Stat("For optimal security, a password should be at least 16 characters long.", "CISA"),
        new Stat("About 88% of people utilize multifactor authentication.", "GoodFirms"),
        new Stat("At least 70% of security experts believe password managers are the safest way to manage passwords.", "GoodFirms"),
        new Stat("Password strength can be measured with a formula for password entropy, which estimates how long a password would take to crack.", "TechTarget")

    );
    
}

