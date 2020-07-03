package fr.nicoevgl.escapegameonline;

public interface IAttacker {
    int[] generateCombi();

    int[] generateProp();

    int[] generateNewProp(int[] secretCombination, int[] firstProposition);
}
