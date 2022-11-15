package lotto;

import static lotto.Constant.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Rank {
    private final int bonusNumber;
    private final Lotto lottoNumbers;

    public Rank() {
        this.lottoNumbers = UI.getLottoNumbers();
        this.bonusNumber = UI.getBonusNumber();
        validateBonusNumber();
    }

    private void validateBonusNumber() {
        List<Integer> lottoNum = new ArrayList<>(lottoNumbers.getNumbers());
        lottoNum.add(bonusNumber);
        lottoNum = lottoNum.stream()
                           .distinct()
                           .collect(Collectors.toList());

        if (lottoNum.size() != (LOTTO_NUMBER_SIZE + 1)) {
            throw new IllegalArgumentException("[ERROR] 당첨번호와 중복되지 않는 보너스 번호를 입력해주세요.");
        }

        Lotto lottoForUsingMethod = new Lotto(lottoNumbers.getNumbers());
        List<Integer> checkBonus = new ArrayList<>(Arrays.asList(bonusNumber));
        lottoForUsingMethod.isNumbersInRange(checkBonus);
    }



    public void run(User user) {
        int countLotto = 0;
        int countBonus = 0;
        for (Lotto lotto : user.userLotto) {
            List<Integer> userNumbers = lotto.getNumbers();
            countLotto = countMatchedNumbers(userNumbers);
            countBonus = countMatchedWithBonusNumber(userNumbers);
            updateResult(countLotto, countBonus);
        }
    }

    private int countMatchedNumbers(List<Integer> userNumbers) {
        int count = 0;
        List<Integer> targetNumbers = lottoNumbers.getNumbers();
        for (Integer number : userNumbers) {
            if (targetNumbers.contains(number)) {
                count++;
            }
        }
        return count;
    }

    private int countMatchedWithBonusNumber(List<Integer> userNumbers) {
        if (userNumbers.contains(bonusNumber)) {
            return 1;
        }
        return 0;
    }

    private void updateResult(int countLotto, int countBonus) {
        if ((countLotto != 5) || (countLotto == 5 && countBonus == 0)) {
            Result result = Result.fromLabel(countLotto);
            result.increaseCount();
            return;
        }

        if (countLotto == 5 && countBonus == 1) {
            Result result = Result.fromLabel(countLotto + 2);
            result.increaseCount();
        }
    }
}

enum Result {
    FST(6, MESSAGE_FIRST_PRIZE, 0),
    SECOND(7, MESSAGE_SECOND_PRIZE, 0),
    THIRD(5, MESSAGE_THIRD_PRIZE, 0),
    FOURTH(4, MESSAGE_FOURTH_PRIZE, 0),
    FIFTH(3, MESSAGE_FIFTH_PRIZE, 0),
    EMPTY(-1, "Empty", 0);
    private final int label;
    private final String text;
    private int count;

    Result(int label, String text, int count) {
        this.label = label;
        this.text = text;
        this.count = count;
    }

    public static Result fromLabel(int targetLabel) {
        return Arrays.stream(Result.values())
                .filter(result -> result.label == targetLabel)
                .findAny()
                .orElse(EMPTY);
    }

    public void increaseCount() {
        this.count = this.count + 1;
    }

    public String text() { return text; }
    public int count() { return count; }
}
