package com.example.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChapterFragment extends Fragment {

    private int chapterNumber;

    public ChapterFragment(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chapter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.chapterTitle);
        TextView content = view.findViewById(R.id.chapterContent);

        switch (chapterNumber) {
            case 1:
                title.setText("Chapter 1");
                content.setText("În câmpia Dunării, cu câțiva ani înaintea celui de-al doilea război mondial, se pare că timpul avea cu oamenii nesfârșită răbdare; viața se scurgea aici fără conflicte mari.\n" +
                        "Era începutul verii.\n" +
                        "Familia Moromete se întorsese mai devreme de la câmp. Cât ajunseseră acasă, Paraschiv, cel mai mare dintre copii, se dăduse jos din căruță, lăsase pe alții să deshame și să dea jos uneltele, iar el întinsese pe prispă o haină veche și se culcase peste ea gemând. La fel făcuse și al doilea fiu, Nilă; intrase în casă și după ce se aruncase într-un pat, începuse și el să geamă, dar mai tare ca fratele său, ca și când ar fi fost bolnav. Al treilea băiat, Achim, se furișase în grajdul cailor, se trântise în iesle să nu-l mai găsească nimeni, iar cele două fete, Tita și Ilinca, plecaseră repede la gârlă să se scalde.");
                break;
            case 2:
                title.setText("Chapter 2");
                content.setText("Băiatul se urni de lângă poartă și intră în tindă. La vatră, femeia se chinuia cu o mână să mestece mămăliga, iar cu alta să prăjească niște ceapă în tigaie. Alături de vatră, prinsă între două cărămizi, clocotea o oală cu ceva verde înăuntru. Femeia dăduse jos căldarea cu mămăligă și o mesteca aprig, încercând din când în când s-o țină pe loc cu talpa piciorului.\n" +
                        "— Veniși cu oile, mă? Du-te repede și prinde-o pe Bisisica... Viu și eu acum să le mulgem... Să torn mămăliga... Unde sunt fetele alea?\n" +
                        "Băiatul se uită la maică-sa și se întoarse alene îndărăt, fără să spună ceva. Ieși în curte și începu să strângă oile spre obor. Câțiva miei săriseră pe prispă și lingeau sare.\n" +
                        "— Câși, mânca-te-ar câinii! zise el apucând mieii de gât și făcându-le vânt de pe prispă.");
                break;
            case 3:
                title.setText("Chapter 3");
                content.setText("Cele două fete ale lui Moromete ajunseseră la gârlă și începuseră să se scalde. Apa era rece, dar în timpul zilei fuseseră la muncă și a doua zi era duminică, era călușul, nu puteau sta fără să se spele.\n" +
                        "— Ce facem, fa? zise cea mai mare, Tita, după ce intră în apă și începu să clănțăne din dinți. E rece ca gheața.\n" +
                        "— Ei, ca gheața, vezi-ți de treabă, așa e la început, răspunse cealaltă intrând în gârlă vitejește.\n" +
                        "Tita se aplecă și o stropi pe neașteptate cu un val de apă. Fata cea mică țipă ascuțit, se strâmbă înfiorată, apoi deodată se aplecă și ea și o împroșcă pe soră-sa de sus până jos. Se bălăciră câtva timp țipând mereu, apoi Tita se opri și strigă:");
                break;
            case 4:
                title.setText("Chapter 4");
                content.setText("— Treceți la masă, ori vreți să vă chem cu lăutari? strigă Catrina Moromete din pragul tindei. Ilie, unde s-au dus fetele alea? Numai tu le-ai dat nas; unde-or fi ele acuma? Sculați în sus! Paraschive, Nilă, voi n-auziți? Niculae, tu ce mai aștepți? Ai băgat nasul între picioare...\n" +
                        "Femeia se opri deodată din vorbit și chipul i se schimonosi de spaimă. Pe alături de ea țâșni Duțulache, câinele, ieșind din tindă cu o bucată mare de ceva alb în gură, pesemne brânză. Femeia îl întrebă:\n" +
                        "— Când ai intrat, lovi-te-ar turbarea? Lasă jos! Lasă jos! Lasă jos, n-auzii?\n" +
                        "— Dă-i apă, zise Moromete liniștit.\n" +
                        "Paraschiv începu să râdă, sculându-se de pe dulamă.\n" +
                        "— Lasă jos, lasă jos, mânca-te-ar câinii, lasă jooos!... striga zadarnic femeia. Câinele pierise în grădină. Acum să mâncați câinele, spuse mai departe Catrina, uitându-se crunt la fiul vitreg care râdea.");
                break;
            default:
                title.setText("Unknown Chapter");
                content.setText("Conținut indisponibil.");
                break;
        }
    }
}