import java.util.*;
import java.io.*;

public class Legesystem{
     static SortertLenkeliste<Lege> legeListe = new SortertLenkeliste<Lege>();
     static Lenkeliste<Resept> reseptListe = new Lenkeliste<Resept>();
     static Lenkeliste<Legemiddel> legemiddelListe = new Lenkeliste<Legemiddel>();
     static Lenkeliste<Pasient> pasientListe = new Lenkeliste<Pasient>();

     private static Lege finnLege(String legeNavn){
       Lege riktigLege = null;

       for(Lege lege : legeListe){
         if(lege.hentNavn().compareTo(legeNavn) == 0){
           riktigLege = lege;
         }
       }
       return riktigLege;
     }


     private static Legemiddel finnLegemiddel(int legemiddelNummer){
       Legemiddel riktigLegemiddel = null;

       for(Legemiddel legemiddel : legemiddelListe){
         if(legemiddel.hentId() == legemiddelNummer){
           riktigLegemiddel = legemiddel;
         }
       }
       return riktigLegemiddel;
     }


     private static Pasient finnPasient(int pasientID){
       Pasient riktigPasient = null;

       for(Pasient pasient : pasientListe){
         if(pasient.hentId() == pasientID){
           riktigPasient = pasient;
         }
       }
       return riktigPasient;
     }

    public static void main(String[] args){


        lesFraFil(new File("inndata.txt"));
        kommandolokke();
    }


    private static void lesFraFil(File fil){
        Scanner scanner = null;
        try{
            scanner = new Scanner(fil);
        }catch(FileNotFoundException e){
            System.out.println("Fant ikke filen, starter opp som et tomt Legesystem");
            return;
        }

        String innlest = scanner.nextLine();


        while(scanner.hasNextLine()){

            String[] info = innlest.split(" ");

            // Legger til alle pasientene i filen
            if(info[1].compareTo("Pasienter") == 0){
                while(scanner.hasNextLine()) {
                    innlest = scanner.nextLine();

                    //Om vi er ferdig med å legge til pasienter, bryt whileløkken,
                    //slik at vi fortsetter til koden for å legge til legemiddler

                    if(innlest.charAt(0) == '#'){
                        break;
                    }

                    String[] pasientinfo = innlest.split(",");
                    pasientListe.leggTil(new Pasient(pasientinfo[0], pasientinfo[1]));


                }

            }

            //Legger inn Legemidlene
            else if(info[1].compareTo("Legemidler") == 0){
                while(scanner.hasNextLine()){
                    innlest = scanner.nextLine();
                    //Om vi er ferdig med å legge til legemidler, bryt whileløkken,
                    //slik at vi fortsetter til koden for å legge til leger
                    if(innlest.charAt(0) == '#'){
                        break;
                    }
                    String[] legemiddel = innlest.split(", ");
                    if(legemiddel[1].compareTo("a") == 0){


                    legemiddelListe.leggTil(new PreparatA(legemiddel[0],
                    Double.parseDouble(legemiddel[2]),
                    Double.parseDouble(legemiddel[3]),
                    Integer.parseInt(legemiddel[4])));


                    }
                    else if(legemiddel[1].compareTo("b") == 0){

                      legemiddelListe.leggTil(new PreparatB(legemiddel[0],
                      Double.parseDouble(legemiddel[2]),
                      Double.parseDouble(legemiddel[3]),
                      Integer.parseInt(legemiddel[4])));

                    }else if (legemiddel[1].compareTo("c") == 0){

                      legemiddelListe.leggTil(new PreparatC(legemiddel[0],
                      Double.parseDouble(legemiddel[2]),
                      Double.parseDouble(legemiddel[3])));

                    }

                }
            }
            //Legger inn leger
            else if(info[1].compareTo("Leger") == 0){
                while(scanner.hasNextLine()){
                    innlest = scanner.nextLine();
                    //Om vi er ferdig med å legge til leger, bryt whileløkken,
                    //slik at vi fortsetter til koden for å legge til resepter
                    if(innlest.charAt(0) == '#'){
                        break;
                    }
                    info = innlest.split(", ");
                    int kontrollid = Integer.parseInt(info[1]);
                    if(kontrollid == 0){

                      legeListe.leggTil(new Lege(info[0]));

                    }else{

                      legeListe.leggTil(new Spesialist(info[0],
                      Integer.parseInt(info[1])));
                    }
                }

            }
            //Legger inn Resepter
            else if(info[1].compareTo("Resepter") == 0){
                while(scanner.hasNextLine()){
                    innlest = scanner.nextLine();
                    info = innlest.split(", ");


                    try{
                      Legemiddel legemiddel = finnLegemiddel(Integer.parseInt(info[0]));


                      Resept resept = finnLege(info[1]).skrivResept(
                      finnLegemiddel(Integer.parseInt(info[0])),
                      finnPasient(Integer.parseInt(info[2])),
                      Integer.parseInt(info[3]));

                      reseptListe.leggTil(resept);

                    }catch(UlovligUtskrift e) {
                      System.out.println(e.getMessage());
                    }


                }
            }
        }
    }

    private static void skrivUt(){
      System.out.println("\n");
      System.out.println("Leger:"  + "\n");
      legeListe.printUt();
      System.out.println("\n");
      System.out.println("Pasienter: " + "\n");
      pasientListe.printUt();
      System.out.println("\n");
      System.out.println("Resepter: " + "\n");
      reseptListe.printUt();
      System.out.println("\n");
      System.out.println("Legemidler: " + "\n");
      legemiddelListe.printUt();
      System.out.println("\n");
    }

    private static void brukResept(){


      Scanner scanner = new Scanner(System.in);
      System.out.println("SKRIV RESEPT");
      System.out.println("Hvilken pasient vil du se resepter for?" + "\n");
      pasientListe.printUt();
      String input = scanner.nextLine();

      Pasient pasient = finnPasient(Integer.parseInt(input));
      System.out.println("Valgt pasient: " + pasient);


      Stabel reseptliste = pasient.hentReseptliste();
      reseptliste.printUt();
      System.out.println("\n");
      System.out.println("Hvilken resept vil du bruke?" + "\n");
      String reseptnummer = scanner.nextLine();

      for(Resept resept : reseptListe){
        if(resept.hentId() == Integer.parseInt(reseptnummer)){
          resept.bruk();
          System.out.println("Brukte resept paa " + resept.hentLegemiddel() + "." +
          "Antall gjenvaerende reit: " + resept.hentReit());
        }
      }
    }

    private static void leggTilObjekt(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n" + "Skriv inn tall og trykk enter." + "\n" + "\n" +
        "0: Legg til ny pasient."  + "\n" +
        "1: Legg til ny lege."  + "\n" +
        "2: Legg til nytt legemiddel."  + "\n" +
        "3: Opprett ny resept."  + "\n" +
        "4: Avslutt."  + "\n");

        String input = scanner.nextLine();


              while(!input.equals("4")){
                switch(input){
                  case "0":
                  System.out.println("OPPRETT NY PASIENT");
                  try{
                    Scanner navn = new Scanner(System.in);
                    System.out.println("Navn: ");
                    String pasientnavn = scanner.nextLine();
                    System.out.println("Fodselsnummer: ");
                    String fodselsnummer = scanner.nextLine();

                    pasientListe.leggTil(new Pasient(pasientnavn, fodselsnummer));
                    System.out.println("\n");
                    System.out.println("PASIENT LAGT TIL" + "\n" + "Pasient " + pasientnavn + " med fodselsnummer "
                    + fodselsnummer + " er lagt til i pasientlsiten.");
                  }catch(Exception e){
                    System.out.println("Dette gikk ikke");
                  }


                  break;

                  case "1":
                  System.out.println("OPPRETT NY LEGE");
                  try{
                    Scanner navn = new Scanner(System.in);
                    System.out.println("Navn: ");
                    String legenavn = scanner.nextLine();
                    System.out.println("Kontrollnummer: ");
                    String kontrollnummer = scanner.nextLine();

                    if(kontrollnummer.equals("0")){
                      legeListe.leggTil(new Lege(legenavn));
                        System.out.println("LEGE LAGT TIL" + "\n" + "Lege: "
                        + legenavn + " er lagt til i legelisten.");
                    }else{
                      legeListe.leggTil(new Spesialist(legenavn, Integer.parseInt(kontrollnummer)));
                      System.out.println("\n");
                      System.out.println("SPESIALIST LAGT TIL" + "\n" + "Lege: "
                      + legenavn + " er lagt til i legelista.");
                  }

                  }catch(Exception e){
                    System.out.println("Dette gikk ikke");
                  }

                  break;

                  case "2":

                  System.out.println("OPPRETT NYTT LEGEMIDDEL");
                  try{
                    Scanner legemiddel = new Scanner(System.in);
                    System.out.println("Preparat (Skriv inn a, b eller c): ");
                    String preparat = scanner.nextLine();


                    if(preparat.equals("a")){
                      Scanner preparatinfo = new Scanner(System.in);
                      System.out.println("Navn: ");
                      String navn = scanner.nextLine();
                      System.out.println("Pris: ");
                      String pris = scanner.nextLine();
                      System.out.println("Virkestoff");
                      String virkestoff = scanner.nextLine();
                      System.out.println("Styrke: ");
                      String styrke = scanner.nextLine();

                      legemiddelListe.leggTil(new PreparatA(navn, Double.parseDouble(pris),
                      Double.parseDouble(virkestoff), Integer.parseInt(styrke)));
                        System.out.println("LEGEMIDDEL LAGT TIL" + "\n" +
                        "Legemiddel: " + navn + "er lagt til i legemiddellisten.");


                    }if(preparat.equals("b")){
                      Scanner preparatinfo = new Scanner(System.in);
                      System.out.println("Navn: ");
                      String navn = scanner.nextLine();
                      System.out.println("Pris: ");
                      String pris = scanner.nextLine();
                      System.out.println("Virkestoff");
                      String virkestoff = scanner.nextLine();
                      System.out.println("Styrke: ");
                      String styrke = scanner.nextLine();

                      legemiddelListe.leggTil(new PreparatB(navn, Double.parseDouble(pris),
                      Double.parseDouble(virkestoff), Integer.parseInt(styrke)));
                        System.out.println("LEGEMIDDEL LAGT TIL" + "\n" +
                        "Legemiddel: " + navn + "er lagt til i legemiddellisten.");

                  }if(preparat.equals("c")){
                    Scanner preparatinfo = new Scanner(System.in);
                    System.out.println("Navn: ");
                    String navn = scanner.nextLine();
                    System.out.println("Pris: ");
                    String pris = scanner.nextLine();
                    System.out.println("Virkestoff");
                    String virkestoff = scanner.nextLine();


                    legemiddelListe.leggTil(new PreparatC(navn, Double.parseDouble(pris),
                    Double.parseDouble(virkestoff)));
                      System.out.println("LEGEMIDDEL LAGT TIL" + "\n" + "Legemiddel:"
                      + navn + "er lagt til i legemiddellisten.");
                  }


                  }catch(Exception e){
                    System.out.println("Dette gikk ikke");
                  }

                  break;

                  case "3":
                  System.out.println("OPPRETT RESEPT");
                  skrivUt();
                  try{
                    Scanner resept = new Scanner(System.in);

                    System.out.println("Legemiddelnummer: ");
                    String legemiddel = scanner.nextLine();
                    System.out.println("Legens navn: ");
                    String utskrivendelege = scanner.nextLine();
                    System.out.println("Pasient: ");
                    String pasient = scanner.nextLine();
                    System.out.println("Reit: ");
                    String reit = scanner.nextLine();

                    Legemiddel riktigLegemiddel = finnLegemiddel(Integer.parseInt(legemiddel));
                    System.out.println(riktigLegemiddel);
                    Lege riktigLege = finnLege(utskrivendelege);
                    System.out.println(riktigLege);
                    Pasient riktigPasient = finnPasient(Integer.parseInt(pasient));
                    System.out.println(riktigPasient);


                    reseptListe.leggTil(riktigLege.skrivResept(riktigLegemiddel, riktigPasient, Integer.parseInt(reit)));
                    System.out.println("\n");
                    System.out.println("RESEPT LAGT TIL" + "\n" + "Legemiddel: " + legemiddel + " skrevet av lege "
                    + utskrivendelege + " for pasient " + pasient + " er lagt til i pasientlsiten.");
                  }catch(Exception e){
                    System.out.println("Kunne ikke opprette resept");
                  }

                  break;

                  case "4":
                  break;

                  default:
                  System.out.println("Ugyldig input");
                  break;

                }

                System.out.println("\n" + "Skriv inn tall og trykk enter." + "\n" + "\n" +
                "0: Legg til ny pasient."  + "\n" +
                "1: Legg til ny lege."  + "\n" +
                "2: Legg til nytt legemiddel."  + "\n" +
                "3: Opprett ny resept."  + "\n" +
                "4: Tilbake til menyen."  + "\n");

                input = scanner.nextLine();
    }
  }

    private static void hentStatistikk(){
      Scanner scanner = new Scanner(System.in);
      System.out.println("\n" + "Skriv inn tall og trykk enter." + "\n" + "\n" +
      "0: Se antall utskrevende resepter på vanedannende legemidler"  + "\n" +
      "1: Se totalt antall utskrevnde resepter på narkotiske legemidler."  + "\n" +
      "2: Statistikk om mulig misbruk"  + "\n" +
      "3: Tilbake til menyen"  + "\n");

      String input = scanner.nextLine();

      while(!input.equals("3")){
        switch(input){
          case "0":
            System.out.println("ANTALL UTSKREVENDE RESEPTER: VANEDANNENDE LEGEMIDLER.");
          try{
            int antallVanedannende = 0;

            for(Legemiddel legemiddel : legemiddelListe){
              if(legemiddel instanceof PreparatB){
                System.out.println(legemiddel);
                antallVanedannende++;
              }

            }

            System.out.println("Antall utskrevende resepter på vanedannende legemidler er: " + antallVanedannende);



    }catch(Exception e){
      System.out.println("Dette gikk ikke");
    }

    break;

    case "1":
    System.out.println("TOTALT ANTALL UTSKREVNE RESEPTER: NARKOTISKE LEGEMIDLER.");
    try{
      int antallNarkotiske = 0;

      for(Legemiddel legemiddel : legemiddelListe){
        if(legemiddel instanceof PreparatA){
          System.out.println(legemiddel);
          antallNarkotiske++;
        }

      }
      System.out.println("Antall utskrevende resepter på narkotiske legemidler er: " + antallNarkotiske);
    }catch(Exception e){
      System.out.println("Dette gikk ikke");
    }

    break;

    case "2":
    System.out.println("STATISTIKK OM MULIG MISBRUK" + "\n");
      try{
        SortertLenkeliste<Lege> leger = new SortertLenkeliste<Lege>();
        int antallNarkotiskePerLege = 0;
        for(Lege lege : legeListe){
          for(Resept resept : lege.utskrevendeResepter){
            if(resept.hentLegemiddel() instanceof PreparatA){
              antallNarkotiskePerLege++;
              leger.leggTil(lege);
            }
          }
        }

        System.out.println("Leger som har skrevet ut en eller flere narkotiske stoffer:" + "\n");
        leger.printUt();
        for(Lege lege : leger){
            System.out.println("Antall narkotiske resepter for lege " + lege.hentNavn() + " er " + antallNarkotiskePerLege);
        }

        Lenkeliste<Pasient> pasienter = new Lenkeliste<Pasient>();
        int antallNarkotiskePerPasient = 0;

        for(Pasient pasient : pasientListe){
          for(Resept resept : pasient.hentReseptliste()){
            if(resept.hentLegemiddel() instanceof PreparatA){
              antallNarkotiskePerPasient++;
              pasienter.leggTil(pasient);
            }
          }
        }

        System.out.println("\n" + "Pasienter som har minst en gyldig resept paa narkotiske legemidler: " + "\n");
        pasienter.printUt();
        for(Pasient pasient : pasienter){
          System.out.println("Antall narkotiske resepter for pasient " + pasient.hentNavn() + " er " + antallNarkotiskePerPasient);
        }

      }catch(Exception e){
        System.out.println("Dette gikk ikke");
      }

    break;

    case "3":
    break;
    }

    System.out.println("\n" + "Skriv inn tall og trykk enter." + "\n" + "\n" +
    "0: Se antall utskrevende resepter på vanedannende legemidler"  + "\n" +
    "1: Se totalt antall utskrevnde resepter på narkotiske legemidler."  + "\n" +
    "2: Statistikk om mulig misbruk"  + "\n" +
    "3: Tilbake til menyen"  + "\n");

    input = scanner.nextLine();
  }

}

    private static void kommandolokke(){

      Scanner scanner = new Scanner(System.in);

      System.out.println("\n" + "Velkommen! Skriv inn tall og trykk enter." + "\n" +  "\n" +
      "0: Skriver ut oversikt over legesystemet."  + "\n" +
      "1: Opprett ny lege, pasient, resept eller legemiddel."  + "\n" +
      "2: Bruk resept."  + "\n" +
      "3: Se statistikk."  + "\n" +
      "4: Avslutt."  + "\n");

      String input = scanner.nextLine();

      while(!input.equals("4")){
        switch(input){
          case "0":
          skrivUt();
          break;

          case "1":
          leggTilObjekt();
          break;

          case "2":
          brukResept();
          break;

          case "3":
          hentStatistikk();
          break;

          case "4":
          break;

          default:
          System.out.println("Ugyldig input");
          break;

        }


        System.out.println("\n" + "Velkommen! Skriv inn tall og trykk enter." + "\n" +  "\n" +
        "0: Skriver ut oversikt over legesystemet."  + "\n" +
        "1: Opprett ny lege, pasient, resept eller legemiddel."  + "\n" +
        "2: Bruk resept."  + "\n" +
        "3: Se statistikk."  + "\n" +
        "4: Avslutt."  + "\n");

        input = scanner.nextLine();
      }


    }


}
